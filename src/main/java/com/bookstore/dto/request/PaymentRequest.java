package com.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull
    private Long orderId;

    // UPI / CARD / NET_BANKING / WALLET / COD
    @NotBlank
    private String paymentMethod;

    // Optional extra details from frontend
    private String upiId;
    private String cardNumber;
    private String cardHolder;
    private String expiry;
    private String cvv;
    private String bankName;
    private String walletName;
}