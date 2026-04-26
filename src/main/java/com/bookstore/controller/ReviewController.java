package com.bookstore.controller;

import com.bookstore.dto.request.ReviewRequest;
import com.bookstore.dto.response.ReviewResponse;
import com.bookstore.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "⭐ Reviews", description = "Book reviews and ratings (1-5 stars)")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/reviews")
    @Operation(summary = "Add review for a book",
            description = "Logged-in user adds a rating + comment for a book.")
    @SecurityRequirement(name = "Bearer Authentication")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
            {
              "bookId": 1,
              "rating": 5,
              "comment": "Excellent book! Must read for every developer."
            }
            """))
    )
    public ResponseEntity<ReviewResponse> addReview(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.addReview(user.getUsername(), request));
    }

    @GetMapping("/api/reviews/book/{bookId}")
    @Operation(summary = "Get all reviews for a book",
            description = "Public — no login needed. Returns all reviews for a book.")
    @ApiResponse(responseCode = "200", description = "✅ Reviews returned")
    public ResponseEntity<List<ReviewResponse>> getBookReviews(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId));
    }

    @PutMapping("/api/reviews/{id}")
    @Operation(summary = "Update own review")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ReviewResponse> updateReview(
            @AuthenticationPrincipal UserDetails user,
            @Parameter(description = "Review ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(
                reviewService.updateReview(user.getUsername(), id, request));
    }

    @DeleteMapping("/api/reviews/{id}")
    @Operation(summary = "Delete own review")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "204", description = "✅ Review deleted")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        reviewService.deleteReview(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/admin/reviews/{id}")
    @Operation(summary = "Delete any review (Admin)",
            description = "Admin only — moderate and remove inappropriate reviews.")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> adminDeleteReview(@PathVariable Long id) {
        reviewService.adminDeleteReview(id);
        return ResponseEntity.noContent().build();
    }
}