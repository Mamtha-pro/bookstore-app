package com.bookstore.service.impl;

import com.bookstore.dto.request.AddToCartRequest;
import com.bookstore.dto.response.WishlistResponse;
import com.bookstore.entity.*;
import com.bookstore.exception.*;
import com.bookstore.mapper.WishlistMapper;
import com.bookstore.repository.*;
import com.bookstore.service.CartService;
import com.bookstore.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private static final Logger log =
            LoggerFactory.getLogger(WishlistServiceImpl.class);

    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository         userRepository;
    private final BookRepository         bookRepository;
    private final CartService            cartService;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional
    public WishlistResponse addToWishlist(String email, Long bookId) {
        User user = getUser(email);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found: " + bookId));

        List<WishlistItem> existing = wishlistItemRepository.findByUser(user);
        boolean alreadyExists = existing.stream()
                .anyMatch(w -> w.getBook().getId().equals(bookId));

        if (alreadyExists)
            throw new BadRequestException("Book already in wishlist");

        WishlistItem item = WishlistItem.builder()
                .user(user).book(book).build();

        WishlistItem saved = wishlistItemRepository.save(item);
        log.info("Book " + bookId + " added to wishlist for: " + email);
        return WishlistMapper.toResponse(saved);
    }

    @Override
    public List<WishlistResponse> getWishlist(String email) {
        User user = getUser(email);
        return wishlistItemRepository.findByUser(user)
                .stream()
                .map(WishlistMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeFromWishlist(String email, Long wishlistItemId) {
        User user = getUser(email);
        WishlistItem item = wishlistItemRepository
                .findByIdAndUser(wishlistItemId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wishlist item not found"));
        wishlistItemRepository.delete(item);
        log.info("Wishlist item " + wishlistItemId + " removed for: " + email);
    }

    @Override
    @Transactional
    public void moveToCart(String email, Long wishlistItemId) {
        User user = getUser(email);
        WishlistItem item = wishlistItemRepository
                .findByIdAndUser(wishlistItemId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wishlist item not found"));

        AddToCartRequest req = new AddToCartRequest();
        req.setBookId(item.getBook().getId());
        req.setQuantity(1);
        cartService.addToCart(email, req);

        wishlistItemRepository.delete(item);
        log.info("Wishlist item " + wishlistItemId + " moved to cart for: " + email);
    }
}