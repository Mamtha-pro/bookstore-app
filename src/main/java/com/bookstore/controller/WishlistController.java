package com.bookstore.controller;

import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.request.WishlistRequest;
import com.bookstore.dto.response.WishlistResponse;
import com.bookstore.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "❤️ Wishlist", description = "Save books for later and move to cart")
@SecurityRequirement(name = "Bearer Authentication")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get my wishlist",
            description = "Returns all books saved in the wishlist.")
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> getWishlist(
            @AuthenticationPrincipal UserDetails user) {
        List<WishlistResponse> wishlist = wishlistService.getWishlist(user.getUsername());
        return ResponseEntity.ok(
                ApiResponse.success(wishlist, "Wishlist retrieved successfully", 200));
    }

    @PostMapping("/add")
    @Operation(summary = "Add book to wishlist")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Added to wishlist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Book already in wishlist")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
            {
              "bookId": 1
            }
            """))
    )
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody WishlistRequest request) {
        WishlistResponse item = wishlistService.addToWishlist(user.getUsername(), request.getBookId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(item, "Book added to wishlist", 201));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove from wishlist")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Removed from wishlist")
    public ResponseEntity<ApiResponse<Void>> remove(
            @AuthenticationPrincipal UserDetails user,
            @Parameter(description = "Wishlist item ID", example = "1")
            @PathVariable Long id) {
        wishlistService.removeFromWishlist(user.getUsername(), id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Removed from wishlist", 200));
    }

    @PostMapping("/{id}/move-to-cart")
    @Operation(summary = "Move wishlist item to cart",
            description = "Moves book from wishlist directly into shopping cart.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Moved to cart")
    public ResponseEntity<ApiResponse<Void>> moveToCart(
            @AuthenticationPrincipal UserDetails user,
            @Parameter(description = "Wishlist item ID", example = "1")
            @PathVariable Long id) {
        wishlistService.moveToCart(user.getUsername(), id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Moved to cart successfully", 200));
    }
}