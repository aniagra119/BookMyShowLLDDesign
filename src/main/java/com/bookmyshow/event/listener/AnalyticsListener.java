package com.bookmyshow.event.listener;

import com.bookmyshow.event.BookingCancelledEvent;
import com.bookmyshow.event.BookingConfirmedEvent;
import com.bookmyshow.event.BookingEventListener;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AnalyticsListener implements BookingEventListener {
    private final AtomicInteger totalConfirmedBookings = new AtomicInteger(0);
    private final AtomicInteger totalCancelledBookings = new AtomicInteger(0);
    private final AtomicReference<Double> totalGrossRevenue = new AtomicReference<>(0.0);

    @Override
    public void onBookingConfirmed(BookingConfirmedEvent event) {
        totalConfirmedBookings.incrementAndGet();
        totalGrossRevenue.updateAndGet(r -> r + event.getBooking().getTotalAmount());
    }

    @Override
    public void onBookingCancelled(BookingCancelledEvent event) {
        totalCancelledBookings.incrementAndGet();
        totalGrossRevenue.updateAndGet(r -> Math.max(0.0, r - event.getBooking().getTotalAmount()));
    }

    public int getTotalConfirmedBookings() {
        return totalConfirmedBookings.get();
    }

    public int getTotalCancelledBookings() {
        return totalCancelledBookings.get();
    }

    public double getTotalGrossRevenue() {
        return totalGrossRevenue.get();
    }
}
