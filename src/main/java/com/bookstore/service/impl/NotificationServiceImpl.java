package com.bookstore.service.impl;

import com.bookstore.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl {

    // ── Welcome Email ────────────────────────────────────────────────
    @EventListener
    @Async
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("📧 [EMAIL] Welcome email → {} ({})",
                event.getName(), event.getEmail());
        // TODO: inject JavaMailSender and send real email
    }

    // ── Order Confirmation ───────────────────────────────────────────
    @EventListener
    @Async
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("📧 [EMAIL] Order confirmation → {} | Order #{} | ₹{}",
                event.getUserEmail(), event.getOrderId(), event.getTotalAmount());
    }

    // ── Payment Receipt ──────────────────────────────────────────────
    @EventListener
    @Async
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("📧 [EMAIL] Payment receipt → {} | ₹{} | TXN: {}",
                event.getUserEmail(), event.getAmount(), event.getTransactionId());
    }
}