package com.bookstore.service.impl;

import com.bookstore.dto.request.ReviewRequest;
import com.bookstore.dto.response.ReviewResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.Review;
import com.bookstore.entity.User;
import com.bookstore.exception.BadRequestException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.ReviewRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    // ✅ Manual logger — no Lombok needed
    private static final Logger log =
            LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository reviewRepository;
    private final UserRepository   userRepository;
    private final BookRepository   bookRepository;

    // ── Helper: Entity → DTO ──────────────────────────────────────────
    private ReviewResponse toResponse(Review r) {
        ReviewResponse res = new ReviewResponse();
        res.setId(r.getId());
        res.setBookId(r.getBook().getId());
        res.setUserName(r.getUser().getName());
        res.setRating(r.getRating());
        res.setComment(r.getComment());
        res.setCreatedAt(r.getCreatedAt());
        return res;
    }

    // ── Helper: Get User by Email ─────────────────────────────────────
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + email));
    }

    // ── Add Review ────────────────────────────────────────────────────
    @Override
    @Transactional
    public ReviewResponse addReview(String email, ReviewRequest request) {

        User user = getUser(email);

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found: " + request.getBookId()));

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);

        // ✅ Fix 1: String concat instead of {} to avoid ambiguous log
        log.info("Review added for book: " + request.getBookId()
                + " by user: " + email);

        return toResponse(saved);
    }

    // ── Get Reviews for a Book ────────────────────────────────────────
    @Override
    public List<ReviewResponse> getReviewsByBook(Long bookId) {

        // Verify book exists
        bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found: " + bookId));

        return reviewRepository.findByBookId(bookId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Update Own Review ─────────────────────────────────────────────
    @Override
    @Transactional
    public ReviewResponse updateReview(String email,
                                       Long reviewId,
                                       ReviewRequest request) {
        User user = getUser(email);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found: " + reviewId));

        // ✅ Fix 2: Objects.equals() fixes "long cannot be dereferenced"
        // Never use == or .equals() directly on Long fields
        if (!Objects.equals(review.getUser().getId(), user.getId())) {
            throw new BadRequestException("You can only edit your own reviews");
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);

        // ✅ Fix 1: String concat — no ambiguity
        log.info("Review updated: " + reviewId + " by: " + email);

        return toResponse(saved);
    }

    // ── Delete Own Review ─────────────────────────────────────────────
    @Override
    @Transactional
    public void deleteReview(String email, Long reviewId) {

        User user = getUser(email);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found: " + reviewId));

        // ✅ Fix 2: Objects.equals() fixes "long cannot be dereferenced"
        if (!Objects.equals(review.getUser().getId(), user.getId())) {
            throw new BadRequestException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);

        // ✅ Fix 1: String concat — no ambiguity
        log.info("Review deleted: " + reviewId + " by: " + email);
    }

    // ── Admin: Delete Any Review ──────────────────────────────────────
    @Override
    @Transactional
    public void adminDeleteReview(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found: " + reviewId));

        reviewRepository.delete(review);

        // Fix 1: Sty
        log.info("Review deleted by admin: " + reviewId);
    }
}