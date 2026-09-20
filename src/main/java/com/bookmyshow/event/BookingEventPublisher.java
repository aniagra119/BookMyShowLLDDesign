package com.bookmyshow.event;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookingEventPublisher {
    private final List<BookingEventListener> listeners = new CopyOnWriteArrayList<>();

    public void registerListener(BookingEventListener listener) {
        this.listeners.add(Objects.requireNonNull(listener, "listener cannot be null"));
    }

    public void removeListener(BookingEventListener listener) {
        this.listeners.remove(listener);
    }

    public void publishBookingConfirmed(BookingConfirmedEvent event) {
        for (BookingEventListener listener : listeners) {
            listener.onBookingConfirmed(event);
        }
    }

    public void publishBookingCancelled(BookingCancelledEvent event) {
        for (BookingEventListener listener : listeners) {
            listener.onBookingCancelled(event);
        }
    }
}
