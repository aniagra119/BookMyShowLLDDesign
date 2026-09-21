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

    public void publishEvent(BookingEvent event) {
        Objects.requireNonNull(event, "event cannot be null");
        for (BookingEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public void publishBookingConfirmed(BookingConfirmedEvent event) {
        publishEvent(event);
    }

    public void publishBookingCancelled(BookingCancelledEvent event) {
        publishEvent(event);
    }
}
