package com.bookmyshow.model;

import com.bookmyshow.enums.SeatType;
import com.bookmyshow.enums.ShowSeatStatus;
import java.time.Instant;
import java.util.Objects;

public class ShowSeat {
    private final String showSeatId;
    private final String showId;
    private final Seat seat;
    private final SeatType seatType;
    private volatile ShowSeatStatus status;
    private volatile double price;
    private volatile String lockedByUserId;
    private volatile Instant lockedAt;

    public ShowSeat(String showSeatId, String showId, Seat seat, SeatType seatType, double price) {
        this.showSeatId = Objects.requireNonNull(showSeatId, "showSeatId cannot be null");
        this.showId = Objects.requireNonNull(showId, "showId cannot be null");
        this.seat = Objects.requireNonNull(seat, "seat cannot be null");
        this.seatType = Objects.requireNonNull(seatType, "seatType cannot be null");
        this.price = price;
        this.status = ShowSeatStatus.AVAILABLE;
    }

    public String getShowSeatId() {
        return showSeatId;
    }

    public String getShowId() {
        return showId;
    }

    public Seat getSeat() {
        return seat;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public ShowSeatStatus getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLockedByUserId() {
        return lockedByUserId;
    }

    public Instant getLockedAt() {
        return lockedAt;
    }

    public synchronized boolean lock(String userId, Instant timestamp) {
        if (this.status == ShowSeatStatus.AVAILABLE) {
            this.status = ShowSeatStatus.LOCKED;
            this.lockedByUserId = userId;
            this.lockedAt = timestamp;
            return true;
        }
        return false;
    }

    public synchronized void unlock() {
        if (this.status == ShowSeatStatus.LOCKED) {
            this.status = ShowSeatStatus.AVAILABLE;
            this.lockedByUserId = null;
            this.lockedAt = null;
        }
    }

    public synchronized void release() {
        this.status = ShowSeatStatus.AVAILABLE;
        this.lockedByUserId = null;
        this.lockedAt = null;
    }

    public synchronized boolean book(String userId) {
        if (this.status == ShowSeatStatus.LOCKED && Objects.equals(this.lockedByUserId, userId)) {
            this.status = ShowSeatStatus.BOOKED;
            return true;
        }
        return false;
    }

    public synchronized boolean isLockExpired(long timeoutSeconds, Instant now) {
        if (this.status == ShowSeatStatus.LOCKED && this.lockedAt != null) {
            return lockedAt.plusSeconds(timeoutSeconds).isBefore(now);
        }
        return false;
    }
}
