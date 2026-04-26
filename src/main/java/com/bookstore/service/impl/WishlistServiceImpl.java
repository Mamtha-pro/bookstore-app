package com.bookstore.service.impl;

import com.bookstore.dto.response.WishlistResponse;
import com.bookstore.entity.*;
import com.bookstore.exception.*;
import com.bookstore.repository.*;
import com.bookstore.service.CartService;
import com.bookstore.service.WishlistService;
import com.bookstore.dto.request.AddToCartRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository         userRepository;
    private final BookRepository         bookRepository;
    private final CartService            cartService;   // reuse cart service!

    // ── Helper: Entity → DTO ─────────────────────────────────────────
    private WishlistResponse toResponse(WishlistItem item) {
        WishlistResponse r = new WishlistResponse();
        r.setId(item.getId());
        r.setBookId(item.getBook().getId());
        r.setBookTitle(item.getBook().getTitle());
        r.setBookAuthor(item.getBook().getAuthor());
        r.setBookPrice(item.getBook().getPrice());
        r.setBookImageUrl(item.getBook().getImageUrl());
        r.setAddedAt(item.getAddedAt());
        return r;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // ── Add to Wishlist ──────────────────────────────────────────────
    @Override
    @Transactional
    public WishlistResponse addToWishlist(String email, Long bookId) {
        User user = getUser(email);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        // Check if already in wishlist
        List<WishlistItem> existing = wishlistItemRepository.findByUser(user);
        boolean alreadyExists = existing.stream()
                .anyMatch(w -> w.getBook().getId().equals(bookId));
        if (alreadyExists) {
            throw new BadRequestException("Book is already in your wishlist");
        }

        WishlistItem item = WishlistItem.builder()
                .user(user)
                .book(book)
                .build();

        return toResponse(wishlistItemRepository.save(item));
    }

    // ── Get Wishlist ─────────────────────────────────────────────────
    @Override
    public List<WishlistResponse> getWishlist(String email) {
        User user = getUser(email);
        return wishlistItemRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Remove from Wishlist ─────────────────────────────────────────
    @Override
    @Transactional
    public void removeFromWishlist(String email, Long wishlistItemId) {
        User user = getUser(email);
        WishlistItem item = wishlistItemRepository
                .findByIdAndUser(wishlistItemId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));
        wishlistItemRepository.delete(item);
    }

    // ── Move from Wishlist → Cart ────────────────────────────────────
    @Override
    @Transactional
    public void moveToCart(String email, Long wishlistItemId) {
        User user = getUser(email);
        WishlistItem item = wishlistItemRepository
                .findByIdAndUser(wishlistItemId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));

        // Add to cart using existing CartService
        AddToCartRequest cartRequest = new AddToCartRequest();
        cartRequest.setBookId(item.getBook().getId());
        cartRequest.setQuantity(1);
        cartService.addToCart(email, cartRequest);

        // Remove from wishlist after moving
        wishlistItemRepository.delete(item);
    }
}