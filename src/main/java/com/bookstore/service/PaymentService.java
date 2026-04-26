package com.bookstore.service;

import com.bookstore.dto.request.PaymentRequest;
import com.bookstore.dto.request.PaymentVerifyRequest;
import com.bookstore.dto.response.PaymentResponse;
import java.util.List;

public interface PaymentService {
    PaymentResponse initiatePayment(PaymentRequest request);
    PaymentResponse verifyPayment(PaymentVerifyRequest request);
    PaymentResponse refundPayment(Long paymentId);
    PaymentResponse getPaymentByOrderId(Long orderId);
    List<PaymentResponse> getAllPayments();
    PaymentResponse updatePaymentStatus(Long paymentId, String status);
}