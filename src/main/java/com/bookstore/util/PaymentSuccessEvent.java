package com.bookstore.util;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentSuccessEvent extends ApplicationEvent {

    private final Long   paymentId;
    private final String userEmail;
    private final Double amount;        // ✅ Double
    private final String transactionId;

    public PaymentSuccessEvent(Object source,
                               Long paymentId,
                               String userEmail,
                               Double amount,          // ✅ Double
                               String transactionId) {
        super(source);
        this.paymentId     = paymentId;
        this.userEmail     = userEmail;
        this.amount        = amount;
        this.transactionId = transactionId;
    }
}