package com.bookmyshow.model;

import com.bookmyshow.enums.BookingStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Booking {
    private final String bookingId;
    private final String bookingNumber;
    private final User user;
    private final Show show;
    private final List<ShowSeat> bookedSeats;
    private final double totalAmount;
    private volatile BookingStatus status;
    private volatile Payment payment;
    private final Instant bookedAt;

    public Booking(String bookingId, String bookingNumber, User user, Show show, List<ShowSeat> bookedSeats, double totalAmount) {
        this.bookingId = Objects.requireNonNull(bookingId, "bookingId cannot be null");
        this.bookingNumber = Objects.requireNonNull(bookingNumber, "bookingNumber cannot be null");
        this.user = Objects.requireNonNull(user, "user cannot be null");
        this.show = Objects.requireNonNull(show, "show cannot be null");
        this.bookedSeats = new ArrayList<>(Objects.requireNonNull(bookedSeats, "bookedSeats cannot be null"));
        this.totalAmount = totalAmount;
        this.status = BookingStatus.PENDING;
        this.bookedAt = Instant.now();
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public User getUser() {
        return user;
    }

    public Show getShow() {
        return show;
    }

    public List<ShowSeat> getBookedSeats() {
        return Collections.unmodifiableList(bookedSeats);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Payment getPayment() {
        return payment;
    }

    public Instant getBookedAt() {
        return bookedAt;
    }

    public synchronized void confirm(Payment payment) {
        this.payment = Objects.requireNonNull(payment, "payment cannot be null");
        this.status = BookingStatus.CONFIRMED;
    }

    public synchronized void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    public synchronized void expire() {
        this.status = BookingStatus.EXPIRED;
    }
}
