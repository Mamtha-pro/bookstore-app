package com.bookstore.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;

@Service
public class RazorpayService {

    // Manual logger
    private static final Logger log =
            LoggerFactory.getLogger(RazorpayService.class);

    @Value("${razorpay.key.id:rzp_test_placeholder}")
    private String keyId;

    @Value("${razorpay.key.secret:placeholder_secret_key_here}")
    private String keySecret;

    // ── Create Order ──────────────────────────────────────────────────
//    public JSONObject createOrder(Double amount) throws RazorpayException {
//        RazorpayClient client = new RazorpayClient(keyId, keySecret);
//
//        JSONObject options = new JSONObject();
//        options.put("amount", (int)(amount * 100));
//        options.put("currency", "INR");
//        options.put("payment_capture", 1);
//
//        Order order = client.orders.create(options);

//        // ✅ String concat — no ambiguity
//        log.info("Razorpay order created: " + order.get("id"));
//        return order.toJson();
//    }

    // ── Verify Signature ──────────────────────────────────────────────
    public boolean verifySignature(String razorpayOrderId,
                                   String razorpayPaymentId,
                                   String razorpaySignature) {
        try {
            String payload   = razorpayOrderId + "|" + razorpayPaymentId;
            String generated = hmacSha256(payload, keySecret);
            boolean valid    = generated.equals(razorpaySignature);

            // ✅ String concat — no ambiguity
            log.info("Signature check: " + (valid ? "VALID" : "INVALID"));
            return valid;

        } catch (Exception e) {
            // ✅ String concat — no ambiguity
            log.error("Signature error: " + e.getMessage());
            return false;
        }
    }
//
//    // ── Fetch Payment ─────────────────────────────────────────────────
//    public JSONObject fetchPayment(String paymentId) throws RazorpayException {
//        RazorpayClient client = new RazorpayClient(keyId, keySecret);
//        Payment payment = client.payments.fetch(paymentId);
//
//        // ✅ String concat — no ambiguity
//        log.info("Fetched payment: " + paymentId);
//        return payment.toJson();
//    }
//
//    // ── Refund ────────────────────────────────────────────────────────
//    public JSONObject refund(String paymentId,
//                             Double amount) throws RazorpayException {
//        RazorpayClient client = new RazorpayClient(keyId, keySecret);
//
//        JSONObject req = new JSONObject();
//        req.put("amount", (int)(amount * 100));
//
//        Refund refund = client.payments.refund(paymentId, req);
//
//        // ✅ String concat — no ambiguity
//        log.info("Refund initiated: " + refund.get("id"));
//        return refund.toJson();
//    }

    // ── Get Key ID ────────────────────────────────────────────────────
    public String getKeyId() {
        return keyId;
    }

    // ── HMAC SHA256 ───────────────────────────────────────────────────
    private String hmacSha256(String data,
                              String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"));
        byte[] hash = mac.doFinal(
                data.getBytes(StandardCharsets.UTF_8));
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}