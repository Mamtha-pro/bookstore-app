package com.bookstore.controller;

import com.bookstore.dto.request.PlaceOrderRequest;
import com.bookstore.dto.response.OrderResponse;
import com.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequiredArgsConstructor
@Tag(name = "📦 Orders", description = "Place orders and track order history")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/orders")
    @Operation(summary = "Place order from cart",
            description = "Converts current cart into an order. Cart is cleared after.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = " Order placed successfully"),
            @ApiResponse(responseCode = "400", description = " Cart is empty")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
            {
              "address": "123 MG Road, Bengaluru, Karnataka 560001"
            }
            """))
    )
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(user.getUsername(), request));
    }

    @GetMapping("/api/orders")
    @Operation(summary = "Get my orders",
            description = "Returns all orders for the logged-in user, newest first.")
    public List<OrderResponse> getMyOrders(
            @AuthenticationPrincipal UserDetails user) {
        return orderService.getMyOrders(user.getUsername());
    }

    @GetMapping("/api/orders/{id}")
    @Operation(summary = "Get single order details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = " Order found"),
            @ApiResponse(responseCode = "404", description = " Order not found")
    })
    public OrderResponse getOrder(
            @AuthenticationPrincipal UserDetails user,
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id) {
        return orderService.getOrderById(user.getUsername(), id);
    }

    @PutMapping("/api/orders/{id}/cancel")
    @Operation(summary = "Cancel order",
            description = "Cancel a PENDING order. Cannot cancel SHIPPED or DELIVERED orders.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = " Order cancelled"),
            @ApiResponse(responseCode = "400", description = " Order cannot be cancelled")
    })
    public OrderResponse cancelOrder(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        return orderService.cancelOrder(user.getUsername(), id);
    }

    @GetMapping("/api/admin/orders")
    @Operation(summary = "Get ALL orders (Admin)",
            description = "Admin only — view all orders across all users.")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("/api/admin/orders/{id}/status")
    @Operation(summary = "Update order status (Admin)",
            description = """
                   Admin only — update order status.
                   
                   Valid values: `PENDING` `CONFIRMED` `SHIPPED` `DELIVERED` `CANCELLED`
                   """)
    public OrderResponse updateStatus(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "New status",
                    example = "CONFIRMED")
            @RequestParam String status) {
        return orderService.updateOrderStatus(id, status);
    }
}