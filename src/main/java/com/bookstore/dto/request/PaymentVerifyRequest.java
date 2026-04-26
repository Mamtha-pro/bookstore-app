package com.bookstore.dto.request;

import lombok.Data;

@Data
public class PaymentVerifyRequest {
    private Long   orderId;
    private String transactionId;
}