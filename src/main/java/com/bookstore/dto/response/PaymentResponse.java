package com.bookstore.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long   id;
    private Long   orderId;
    private Double amount;
    private String status;
    private String paymentMethod;
    private String transactionId;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String failureReason;
    private String cardLast4;
    private String cardNetwork;
    private String upiId;
    private String bankName;
    private String walletName;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}