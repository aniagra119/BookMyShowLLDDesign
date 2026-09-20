package com.bookmyshow.event;

import com.bookmyshow.model.Booking;
import java.time.Instant;
import java.util.Objects;

public abstract class BookingEvent {
    private final String eventId;
    private final Booking booking;
    private final Instant timestamp;

    public BookingEvent(String eventId, Booking booking, Instant timestamp) {
        this.eventId = Objects.requireNonNull(eventId, "eventId cannot be null");
        this.booking = Objects.requireNonNull(booking, "booking cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp cannot be null");
    }

    public String getEventId() {
        return eventId;
    }

    public Booking getBooking() {
        return booking;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
