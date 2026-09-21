package com.bookmyshow.repository;

import com.bookmyshow.model.Booking;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(String bookingId);
    List<Booking> findAll();
    List<Booking> findByUserId(String userId);
}
