package com.bookstore.mapper;

import com.bookstore.dto.response.ReviewResponse;
import com.bookstore.entity.Review;

public class ReviewMapper {

    private ReviewMapper() {}

    public static ReviewResponse toResponse(Review review) {
        if (review == null) return null;

        ReviewResponse r = new ReviewResponse();
        r.setId(review.getId());
        r.setBookId(review.getBook().getId());
        r.setUserName(review.getUser().getName());
        r.setRating(review.getRating());
        r.setComment(review.getComment());
        r.setCreatedAt(review.getCreatedAt());
        return r;
    }
}
