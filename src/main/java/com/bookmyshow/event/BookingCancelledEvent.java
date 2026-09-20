package com.bookmyshow.event;

import com.bookmyshow.model.Booking;
import java.time.Instant;

public class BookingCancelledEvent extends BookingEvent {
    public BookingCancelledEvent(String eventId, Booking booking, Instant timestamp) {
        super(eventId, booking, timestamp);
    }
}
