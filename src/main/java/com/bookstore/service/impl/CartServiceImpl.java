package com.bookstore.service.impl;

import com.bookstore.dto.request.AddToCartRequest;
import com.bookstore.dto.response.*;
import com.bookstore.entity.*;
import com.bookstore.exception.*;
import com.bookstore.repository.*;
import com.bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository     cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository     bookRepository;
    private final UserRepository     userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }

    private CartResponse toResponse(Cart cart) {
        // ← load items directly from DB, never rely on JPA cache
        List<CartItem> items = cartItemRepository.findByCart(cart);

        List<CartItemResponse> itemResponses = items.stream().map(item -> {
            CartItemResponse r = new CartItemResponse();
            r.setItemId(item.getId());
            r.setBookId(item.getBook().getId());
            r.setBookTitle(item.getBook().getTitle());
            r.setQuantity(item.getQuantity());
            r.setUnitPrice(item.getUnitPrice());
            r.setSubtotal(item.getUnitPrice() * item.getQuantity());
            return r;
        }).collect(Collectors.toList());

        double total = itemResponses.stream()
                .mapToDouble(CartItemResponse::getSubtotal).sum();

        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setItems(itemResponses);
        response.setTotalAmount(total);
        return response;
    }

    @Override
    public CartResponse getCart(String email) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(String email, AddToCartRequest request) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        // Check if book already in cart
        CartItem existing = cartItemRepository.findByCart(cart)
                .stream()
                .filter(i -> i.getBook().getId().equals(request.getBookId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            // ← update quantity and save directly
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            cartItemRepository.save(existing);
        } else {
            // ← save new item directly, don't rely on cascade
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(request.getQuantity())
                    .unitPrice(book.getPrice())
                    .build();
            cartItemRepository.save(newItem);
        }

        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(String email, Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return toResponse(item.getCart());
    }

    @Override
    @Transactional
    public void removeCartItem(String email, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearCart(String email) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        List<CartItem> items = cartItemRepository.findByCart(cart);
        cartItemRepository.deleteAll(items);
    }
}