package com.bookstore.service.impl;

import com.bookstore.constants.OrderStatus;
import com.bookstore.dto.request.PlaceOrderRequest;
import com.bookstore.dto.response.OrderResponse;
import com.bookstore.entity.*;
import com.bookstore.exception.BadRequestException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.OrderMapper;
import com.bookstore.repository.*;
import com.bookstore.service.OrderService;
import com.bookstore.util.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository           orderRepository;
    private final CartRepository            cartRepository;
    private final CartItemRepository        cartItemRepository;
    private final UserRepository            userRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ── Helper: Get User ──────────────────────────────────────────────
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + email));
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
                        new BadRequestException(
                                "Your cart is empty! Add books first."));

        // ✅ Load items directly from DB
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        // ✅ Validate cart not empty
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BadRequestException(
                    "Your cart is empty! Add books first.");
        }

        // ✅ Validate address
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

        // ✅ Clear cart properly
        cartItemRepository.deleteAll(cartItems);
        cart.getItems().clear();
        cartRepository.save(cart);

        // ✅ Fire event
        eventPublisher.publishEvent(new OrderPlacedEvent(
                this,
                saved.getId(),
                user.getEmail(),
                saved.getTotalAmount()));

        log.info("Order placed: " + saved.getId() + " by: " + email);
        return OrderMapper.toResponse(saved);
    }

    // ── Get My Orders ─────────────────────────────────────────────────
    @Override
    public List<OrderResponse> getMyOrders(String email) {
        User user = getUser(email);
        return orderRepository
                .findByUserOrderByOrderedAtDesc(user)
                .stream()
                .map(OrderMapper::toResponse)
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
        return OrderMapper.toResponse(order);
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

        log.info("Order cancelled: " + orderId + " by: " + email);
        return OrderMapper.toResponse(saved);
    }

    // ── Admin: All Orders ─────────────────────────────────────────────
    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toResponse)
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

        log.info("Order status updated: " + orderId + " to: " + status);
        return OrderMapper.toResponse(saved);
    }
}