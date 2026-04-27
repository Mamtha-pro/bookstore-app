package com.bookstore.controller;

import com.bookstore.dto.request.AddToCartRequest;
import com.bookstore.dto.response.CartResponse;
import com.bookstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @ApiResponse(responseCode = "200", description = " Cart returned")
    public CartResponse getCart(
            @AuthenticationPrincipal UserDetails user) {
        return cartService.getCart(user.getUsername());
    }

    @PostMapping("/add")
    @Operation(summary = "Add book to cart",
            description = "Adds a book to cart. If book already in cart, increases quantity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = " Item added to cart"),
            @ApiResponse(responseCode = "404", description = " Book not found")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
            {
              "bookId": 1,
              "quantity": 2
            }
            """))
    )
    public CartResponse addToCart(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody AddToCartRequest request) {
        return cartService.addToCart(user.getUsername(), request);
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Update item quantity",
            description = "Change the quantity of a cart item.")
    public CartResponse updateItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return cartService.updateCartItem(user.getUsername(), id, quantity);
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove item from cart")
    @ApiResponse(responseCode = "204", description = " Item removed")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        cartService.removeCartItem(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear entire cart",
            description = "Removes ALL items from the cart.")
    @ApiResponse(responseCode = "204", description = " Cart cleared")
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal UserDetails user) {
        cartService.clearCart(user.getUsername());
        return ResponseEntity.noContent().build();
    }
}