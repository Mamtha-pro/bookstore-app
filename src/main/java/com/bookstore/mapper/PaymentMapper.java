package com.bookstore.mapper;

import com.bookstore.dto.response.PaymentResponse;
import com.bookstore.entity.Payment;

public class PaymentMapper {

    private PaymentMapper() {}

    public static PaymentResponse toResponse(Payment payment) {
        if (payment == null) return null;

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .gatewayPaymentId(payment.getGatewayPaymentId())
                .failureReason(payment.getFailureReason())
                .cardLast4(payment.getCardLast4())
                .cardNetwork(payment.getCardNetwork())
                .upiId(payment.getUpiId())
                .bankName(payment.getBankName())
                .walletName(payment.getWalletName())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
