package com.bookmyshow.repository;

import com.bookmyshow.model.Booking;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBookingRepository implements BookingRepository {
    private final Map<String, Booking> storage = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        Objects.requireNonNull(booking, "booking cannot be null");
        storage.put(booking.getBookingId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(String bookingId) {
        if (bookingId == null) return Optional.empty();
        return Optional.ofNullable(storage.get(bookingId));
    }

    @Override
    public List<Booking> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    @Override
    public List<Booking> findByUserId(String userId) {
        if (userId == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(b -> b.getUser() != null && userId.equals(b.getUser().getUserId()))
                .toList();
    }
}
