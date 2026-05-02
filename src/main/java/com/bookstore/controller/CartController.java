package com.bookstore.controller;

import com.bookstore.dto.response.ApiResponse;
import com.bookstore.dto.request.AddToCartRequest;
import com.bookstore.dto.response.CartResponse;
import com.bookstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "🛒 Cart", description = "Shopping cart — add, update, remove items")
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "View my cart",
            description = "Returns current user's cart with all items and total amount.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Cart returned")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal UserDetails user) {
        CartResponse cart = cartService.getCart(user.getUsername());
        return ResponseEntity.ok(
                ApiResponse.success(cart, "Cart retrieved successfully", 200));
    }

    @PostMapping("/add")
    @Operation(summary = "Add book to cart",
            description = "Adds a book to cart. If book already in cart, increases quantity.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Item added to cart"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Book not found")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
            {
              "bookId": 1,
              "quantity": 2
            }
            """)))
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addToCart(user.getUsername(), request);
        return ResponseEntity.ok(
                ApiResponse.success(cart, "Item added to cart", 200));
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Update item quantity",
            description = "Change the quantity of a cart item.")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        CartResponse cart = cartService.updateCartItem(user.getUsername(), id, quantity);
        return ResponseEntity.ok(
                ApiResponse.success(cart, "Cart item updated", 200));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove item from cart")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204", description = "Item removed")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        cartService.removeCartItem(user.getUsername(), id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Item removed from cart", 200));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear entire cart",
            description = "Removes ALL items from the cart.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Cart cleared")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal UserDetails user) {
        cartService.clearCart(user.getUsername());
        return ResponseEntity.ok(
                ApiResponse.success(null, "Cart cleared", 200));
    }
}