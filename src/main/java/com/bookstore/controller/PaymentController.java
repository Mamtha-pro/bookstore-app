package com.bookstore.controller;

import com.bookstore.dto.request.PaymentRequest;
import com.bookstore.dto.request.PaymentVerifyRequest;
import com.bookstore.dto.response.PaymentResponse;
import com.bookstore.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "💳 Payments",
        description = "Mock payment — UPI, Card, Net Banking, Wallet, COD")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/api/payments/initiate")
    @Operation(summary = "Initiate payment",
            description = "Processes mock payment instantly. No real money!")
    public ResponseEntity<PaymentResponse> initiate(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.initiatePayment(request));
    }

    @PostMapping("/api/payments/verify")
    @Operation(summary = "Verify payment by transaction ID")
    public ResponseEntity<PaymentResponse> verify(
            @RequestBody PaymentVerifyRequest request) {
        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }

    @GetMapping("/api/payments/{orderId}")
    @Operation(summary = "Get payment status for order")
    public ResponseEntity<PaymentResponse> getStatus(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(
                paymentService.getPaymentByOrderId(orderId));
    }

    @GetMapping("/api/admin/payments")
    @Operation(summary = "Get ALL payments (Admin)")
    public ResponseEntity<List<PaymentResponse>> getAll() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PostMapping("/api/admin/payments/{id}/refund")
    @Operation(summary = "Refund payment (Admin)")
    public ResponseEntity<PaymentResponse> refund(
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }

    @PutMapping("/api/admin/payments/{id}")
    @Operation(summary = "Update payment status (Admin)")
    public ResponseEntity<PaymentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(id, status));
    }
}