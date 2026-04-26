package com.bookstore.service.impl;

import com.bookstore.constants.OrderStatus;
import com.bookstore.constants.PaymentStatus;
import com.bookstore.dto.request.PaymentRequest;
import com.bookstore.dto.request.PaymentVerifyRequest;
import com.bookstore.dto.response.PaymentResponse;
import com.bookstore.entity.Order;
import com.bookstore.entity.Payment;
import com.bookstore.exception.BadRequestException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.PaymentRepository;
import com.bookstore.service.PaymentService;
import com.bookstore.util.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository         paymentRepository;
    private final OrderRepository           orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ── Entity → DTO ──────────────────────────────────────────────────
    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .orderId(p.getOrder().getId())
                .amount(p.getAmount())
                .status(p.getStatus().name())
                .paymentMethod(p.getPaymentMethod())
                .transactionId(p.getTransactionId())
                .gatewayPaymentId(p.getGatewayPaymentId())
                .failureReason(p.getFailureReason())
                .cardLast4(p.getCardLast4())
                .cardNetwork(p.getCardNetwork())
                .upiId(p.getUpiId())
                .bankName(p.getBankName())
                .walletName(p.getWalletName())
                .paidAt(p.getPaidAt())
                .createdAt(p.getCreatedAt())
                .build();
    }

    // ── Generate Transaction ID ───────────────────────────────────────
    private String generateTxnId() {
        return "TXN" + System.currentTimeMillis()
                + UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
    }

    // ── Detect Card Network ───────────────────────────────────────────
    private String detectCardNetwork(String cardNumber) {
        if (cardNumber == null) return "VISA";
        String num = cardNumber.replaceAll("\\s", "");
        if (num.startsWith("4"))                    return "VISA";
        if (num.startsWith("5"))                    return "MASTERCARD";
        if (num.startsWith("6"))                    return "RUPAY";
        if (num.startsWith("34") || num.startsWith("37")) return "AMEX";
        return "VISA";
    }

    // ── INITIATE PAYMENT ──────────────────────────────────────────────
    @Override
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        // Block duplicate successful payment
        paymentRepository.findByOrderId(request.getOrderId())
                .ifPresent(existing -> {
                    if (existing.getStatus() == PaymentStatus.SUCCESS)
                        throw new BadRequestException("Order already paid!");
                });

        String method = request.getPaymentMethod().toUpperCase();
        String txnId  = generateTxnId();

        Payment.PaymentBuilder builder = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(method)
                .transactionId(txnId)
                .gatewayPaymentId("MOCK_" + txnId)
                .paidAt(LocalDateTime.now());

        // ── Set status and method-specific fields ─────────────────────
        if ("COD".equals(method)) {
            builder.status(PaymentStatus.PENDING);

        } else if ("UPI".equals(method)) {
            builder.status(PaymentStatus.SUCCESS);
            String upiId = request.getUpiId() != null
                    ? request.getUpiId() : "user@upi";
            builder.upiId(upiId);

        } else if ("CARD".equals(method)) {
            builder.status(PaymentStatus.SUCCESS);
            String cardNum = request.getCardNumber() != null
                    ? request.getCardNumber().replaceAll("\\s", "")
                    : "4111111111111111";
            String last4 = cardNum.length() >= 4
                    ? cardNum.substring(cardNum.length() - 4) : "1111";
            builder.cardLast4(last4);
            builder.cardNetwork(detectCardNetwork(cardNum));

        } else if ("NET_BANKING".equals(method)) {
            builder.status(PaymentStatus.SUCCESS);
            builder.bankName(request.getBankName() != null
                    ? request.getBankName() : "State Bank of India");

        } else if ("WALLET".equals(method)) {
            builder.status(PaymentStatus.SUCCESS);
            builder.walletName(request.getWalletName() != null
                    ? request.getWalletName() : "Paytm");

        } else {
            builder.status(PaymentStatus.SUCCESS);
        }

        Payment saved = paymentRepository.save(builder.build());

        // Confirm order
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // Fire notification for non-COD
        if (!"COD".equals(method)) {
            eventPublisher.publishEvent(new PaymentSuccessEvent(
                    this,
                    saved.getId(),
                    order.getUser().getEmail(),
                    saved.getAmount(),
                    saved.getTransactionId()));
        }

        log.info("Payment processed: " + txnId
                + " method: " + method
                + " order: " + order.getId());

        return toResponse(saved);
    }

    // ── VERIFY PAYMENT ────────────────────────────────────────────────
    @Override
    @Transactional
    public PaymentResponse verifyPayment(PaymentVerifyRequest request) {
        Payment payment = paymentRepository
                .findByTransactionId(request.getTransactionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));
        return toResponse(payment);
    }

    // ── REFUND ────────────────────────────────────────────────────────
    @Override
    @Transactional
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS)
            throw new BadRequestException(
                    "Only successful payments can be refunded");

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.getOrder().setStatus(OrderStatus.CANCELLED);
        orderRepository.save(payment.getOrder());

        log.info("Refund processed for payment: " + paymentId);
        return toResponse(paymentRepository.save(payment));
    }

    // ── GET BY ORDER ──────────────────────────────────────────────────
    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        return toResponse(
                paymentRepository.findByOrderId(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found for order: "
                                                + orderId)));
    }

    // ── ADMIN: ALL PAYMENTS ───────────────────────────────────────────
    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── ADMIN: UPDATE STATUS ──────────────────────────────────────────
    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(Long paymentId,
                                               String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));
        payment.setStatus(PaymentStatus.valueOf(status.toUpperCase()));
        return toResponse(paymentRepository.save(payment));
    }
}