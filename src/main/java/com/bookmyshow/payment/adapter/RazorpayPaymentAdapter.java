package com.bookmyshow.payment.adapter;

import com.bookmyshow.enums.PaymentMode;
import com.bookmyshow.enums.PaymentStatus;
import com.bookmyshow.model.Payment;
import com.bookmyshow.payment.PaymentProcessor;
import com.bookmyshow.payment.external.ExternalRazorpayClient;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RazorpayPaymentAdapter implements PaymentProcessor {
    private final ExternalRazorpayClient razorpayClient;

    public RazorpayPaymentAdapter(ExternalRazorpayClient razorpayClient) {
        this.razorpayClient = Objects.requireNonNull(razorpayClient, "razorpayClient cannot be null");
    }

    @Override
    public Payment processPayment(String bookingId, double amount, PaymentMode mode) {
        long amountInPaise = Math.round(amount * 100.0);
        String orderId = razorpayClient.createOrder(amountInPaise, "INR", bookingId);
        String paymentId = razorpayClient.capturePayment(orderId, amountInPaise);

        return new Payment(
                "PAY-" + UUID.randomUUID().toString().substring(0, 8),
                bookingId,
                amount,
                mode,
                PaymentStatus.SUCCESS,
                paymentId,
                Instant.now()
        );
    }
}
