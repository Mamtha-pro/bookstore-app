package com.bookstore.service;

import com.bookstore.dto.request.ReviewRequest;
import com.bookstore.dto.response.ReviewResponse;
import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(String email, ReviewRequest request);
    List<ReviewResponse> getReviewsByBook(Long bookId);
    ReviewResponse updateReview(String email, Long reviewId, ReviewRequest request);
    void deleteReview(String email, Long reviewId);
    void adminDeleteReview(Long reviewId);
}