package com.bookmyshow.payment.external;

import java.util.Objects;
import java.util.UUID;

public class ExternalRazorpayClient {
    private final String keyId;
    private final String keySecret;

    public ExternalRazorpayClient(String keyId, String keySecret) {
        this.keyId = Objects.requireNonNull(keyId, "keyId cannot be null");
        this.keySecret = Objects.requireNonNull(keySecret, "keySecret cannot be null");
    }

    public String createOrder(long amountInPaise, String currency, String receipt) {
        if (amountInPaise <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        return "order_rzp_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public String capturePayment(String orderId, long amountInPaise) {
        return "pay_rzp_" + UUID.randomUUID().toString().substring(0, 10);
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        return signature != null && !signature.isBlank();
    }
}
