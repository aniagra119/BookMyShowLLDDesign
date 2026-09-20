package com.bookmyshow.payment;

import com.bookmyshow.enums.PaymentMode;
import com.bookmyshow.model.Payment;

public interface PaymentProcessor {
    Payment processPayment(String bookingId, double amount, PaymentMode mode);
}
