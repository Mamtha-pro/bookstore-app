package com.bookstore.service;



import com.bookstore.dto.request.AddToCartRequest;
import com.bookstore.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(String email);
    CartResponse addToCart(String email, AddToCartRequest request);
    CartResponse updateCartItem(String email, Long itemId, Integer quantity);
    void removeCartItem(String email, Long itemId);
    void clearCart(String email);
}
