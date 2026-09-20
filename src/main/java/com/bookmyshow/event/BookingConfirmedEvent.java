package com.bookmyshow.event;

import com.bookmyshow.model.Booking;
import java.time.Instant;

public class BookingConfirmedEvent extends BookingEvent {
    public BookingConfirmedEvent(String eventId, Booking booking, Instant timestamp) {
        super(eventId, booking, timestamp);
    }
}
