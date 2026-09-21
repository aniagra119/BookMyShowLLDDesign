package com.bookmyshow.model;

import com.bookmyshow.enums.PaymentMode;
import com.bookmyshow.enums.PaymentStatus;
import java.time.Instant;
import java.util.Objects;

public class Payment {
    private final String paymentId;
    private final String bookingId;
    private final double amount;
    private final PaymentMode paymentMode;
    private final PaymentStatus status;
    private final String transactionReference;
    private final Instant timestamp;

    public Payment(String paymentId, String bookingId, double amount, PaymentMode paymentMode, PaymentStatus status, String transactionReference, Instant timestamp) {
        this.paymentId = Objects.requireNonNull(paymentId, "paymentId cannot be null");
        this.bookingId = Objects.requireNonNull(bookingId, "bookingId cannot be null");
        this.amount = amount;
        this.paymentMode = Objects.requireNonNull(paymentMode, "paymentMode cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.transactionReference = Objects.requireNonNull(transactionReference, "transactionReference cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp cannot be null");
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
