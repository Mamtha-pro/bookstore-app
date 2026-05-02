package com.bookstore.service.impl;

import com.bookstore.dto.request.AddToCartRequest;
import com.bookstore.dto.response.CartResponse;
import com.bookstore.entity.*;
import com.bookstore.exception.*;
import com.bookstore.mapper.CartMapper;
import com.bookstore.repository.*;
import com.bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final Logger log =
            LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository     cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository     bookRepository;
    private final UserRepository     userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + email));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }

    @Override
    public CartResponse getCart(String email) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        return CartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(String email, AddToCartRequest request) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found: " + request.getBookId()));

        cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(request.getBookId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(
                                existing.getQuantity() + request.getQuantity()),
                        () -> {
                            CartItem newItem = CartItem.builder()
                                    .cart(cart)
                                    .book(book)
                                    .quantity(request.getQuantity())
                                    .unitPrice(book.getPrice())
                                    .build();
                            cart.getItems().add(newItem);
                        });

        Cart saved = cartRepository.save(cart);
        log.info("Book " + request.getBookId() + " added to cart for: " + email);
        return CartMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(String email,
                                       Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found: " + itemId));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return CartMapper.toResponse(item.getCart());
    }

    @Override
    @Transactional
    public void removeCartItem(String email, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found: " + itemId));
        item.getCart().getItems().remove(item);
        cartItemRepository.delete(item);
        log.info("Cart item " + itemId + " removed for: " + email);
    }

    @Override
    @Transactional
    public void clearCart(String email) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
        log.info("Cart cleared for: " + email);
    }
}