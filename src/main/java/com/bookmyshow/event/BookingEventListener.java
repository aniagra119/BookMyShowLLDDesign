package com.bookmyshow.event;

public interface BookingEventListener {
    void onBookingConfirmed(BookingConfirmedEvent event);
    void onBookingCancelled(BookingCancelledEvent event);
}
