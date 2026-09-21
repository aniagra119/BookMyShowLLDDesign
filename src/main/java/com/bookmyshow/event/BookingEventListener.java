package com.bookmyshow.event;

public interface BookingEventListener {
    default void onEvent(BookingEvent event) {
        if (event instanceof BookingConfirmedEvent confirmedEvent) {
            onBookingConfirmed(confirmedEvent);
        } else if (event instanceof BookingCancelledEvent cancelledEvent) {
            onBookingCancelled(cancelledEvent);
        }
    }

    default void onBookingConfirmed(BookingConfirmedEvent event) {}
    default void onBookingCancelled(BookingCancelledEvent event) {}
}
