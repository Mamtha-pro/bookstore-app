package com.bookstore.util;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderPlacedEvent extends ApplicationEvent {
    private final Long   orderId;
    private final String userEmail;
    private final Double totalAmount;

    public OrderPlacedEvent(Object source, Long orderId,
                            String userEmail, Double totalAmount) {
        super(source);
        this.orderId     = orderId;
        this.userEmail   = userEmail;
        this.totalAmount = totalAmount;
    }
}