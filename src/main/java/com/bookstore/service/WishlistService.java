package com.bookstore.service;

import com.bookstore.dto.response.WishlistResponse;
import java.util.List;

public interface WishlistService {
    WishlistResponse addToWishlist(String email, Long bookId);
    List<WishlistResponse> getWishlist(String email);
    void removeFromWishlist(String email, Long wishlistItemId);
    void moveToCart(String email, Long wishlistItemId);
}