package com.bookstore.service.impl;

import com.bookstore.constants.OrderStatus;
import com.bookstore.dto.request.PlaceOrderRequest;
import com.bookstore.dto.response.OrderItemResponse;
import com.bookstore.dto.response.OrderResponse;
import com.bookstore.entity.*;
import com.bookstore.exception.BadRequestException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.*;
import com.bookstore.service.OrderService;
import com.bookstore.util.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository          orderRepository;
    private final CartRepository           cartRepository;
    private final CartItemRepository       cartItemRepository;  // ← ADD THIS
    private final UserRepository           userRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ── Helper: Get User ──────────────────────────────────────────────
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + email));
    }

    // ── Helper: Entity → DTO ──────────────────────────────────────────
    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = new ArrayList<>();

        if (order.getOrderItems() != null) {
            items = order.getOrderItems().stream()
                    .map(item -> {
                        OrderItemResponse r = new OrderItemResponse();
                        r.setBookId(item.getBook().getId());
                        r.setBookTitle(item.getBook().getTitle());
                        r.setQuantity(item.getQuantity());
                        r.setPrice(item.getPrice());
                        return r;
                    })
                    .collect(Collectors.toList());
        }

        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setStatus(order.getStatus().name());
        r.setTotalAmount(order.getTotalAmount());
        r.setAddress(order.getAddress());
        r.setOrderedAt(order.getOrderedAt());
        r.setItems(items);
        return r;
    }

    // ── Place Order ───────────────────────────────────────────────────
    @Override
    @Transactional
    public OrderResponse placeOrder(String email,
                                    PlaceOrderRequest request) {
        User user = getUser(email);

        // ✅ Fetch cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new BadRequestException("Your cart is empty! Add books first."));

        // ✅ Load items directly from DB (avoids stale JPA cache)
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        // ✅ Check cart has items
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BadRequestException("Your cart is empty! Add books first.");
        }

        // ✅ Check address provided
        if (request.getAddress() == null
                || request.getAddress().trim().isEmpty()) {
            throw new BadRequestException("Delivery address is required!");
        }

        // Build Order
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .address(request.getAddress().trim())
                .totalAmount(0.0)
                .build();

        // Build Order Items from Cart Items
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .book(cartItem.getBook())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        // Calculate total
        double total = orderItems.stream()
                .mapToDouble(oi -> oi.getPrice() * oi.getQuantity())
                .sum();

        order.setTotalAmount(total);
        order.setOrderItems(orderItems);

        Order saved = orderRepository.save(order);

        // ✅ Clear cart properly — delete items directly from DB
        cartItemRepository.deleteAll(cartItems);
        cart.getItems().clear();
        cartRepository.save(cart);

        // ✅ Fire notification event
        eventPublisher.publishEvent(new OrderPlacedEvent(
                this,
                saved.getId(),
                user.getEmail(),
                saved.getTotalAmount()));

        log.info("Order placed: {} by: {}", saved.getId(), email);
        return toResponse(saved);
    }

    // ── Get My Orders ─────────────────────────────────────────────────
    @Override
    public List<OrderResponse> getMyOrders(String email) {
        User user = getUser(email);
        return orderRepository
                .findByUserOrderByOrderedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get Single Order ──────────────────────────────────────────────
    @Override
    public OrderResponse getOrderById(String email, Long orderId) {
        User user = getUser(email);
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found: " + orderId));
        return toResponse(order);
    }

    // ── Cancel Order ──────────────────────────────────────────────────
    @Override
    @Transactional
    public OrderResponse cancelOrder(String email, Long orderId) {
        User user = getUser(email);
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException(
                    "Only PENDING orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);

        log.info("Order cancelled: {} by: {}", orderId, email);
        return toResponse(saved);
    }

    // ── Admin: All Orders ─────────────────────────────────────────────
    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Admin: Update Status ──────────────────────────────────────────
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found: " + orderId));

        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        Order saved = orderRepository.save(order);

        log.info("Order status updated: {} to: {}", orderId, status);
        return toResponse(saved);
    }
}