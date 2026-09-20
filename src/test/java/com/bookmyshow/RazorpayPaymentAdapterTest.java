package com.bookmyshow;

import com.bookmyshow.enums.PaymentMode;
import com.bookmyshow.enums.PaymentStatus;
import com.bookmyshow.model.Payment;
import com.bookmyshow.payment.adapter.RazorpayPaymentAdapter;
import com.bookmyshow.payment.external.ExternalRazorpayClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Razorpay Payment Adapter Tests")
class RazorpayPaymentAdapterTest {

    @Test
    @DisplayName("Should convert amount to paise, create order, capture payment, and return successful Payment")
    void shouldProcessPaymentSuccessfully() {
        ExternalRazorpayClient client = new ExternalRazorpayClient("rzp_test_123", "secret_abc");
        RazorpayPaymentAdapter adapter = new RazorpayPaymentAdapter(client);

        Payment payment = adapter.processPayment("BKG-9999", 550.0, PaymentMode.RAZORPAY_UPI);

        assertNotNull(payment);
        assertEquals("BKG-9999", payment.getBookingId());
        assertEquals(550.0, payment.getAmount());
        assertEquals(PaymentMode.RAZORPAY_UPI, payment.getPaymentMode());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertTrue(payment.getTransactionReference().startsWith("pay_rzp_"));
        assertNotNull(payment.getTimestamp());
    }
}
