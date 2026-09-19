package com.bookmyshow.lock;

import com.bookmyshow.enums.ShowSeatStatus;
import com.bookmyshow.model.ShowSeat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SeatLockManager {
    private final long lockTimeoutSeconds;
    private final ConcurrentMap<String, Object> seatLocks = new ConcurrentHashMap<>();

    public SeatLockManager() {
        this(600); // 10 minutes default
    }

    public SeatLockManager(long lockTimeoutSeconds) {
        this.lockTimeoutSeconds = lockTimeoutSeconds;
    }

    public boolean lockSeats(List<ShowSeat> seats, String userId) {
        Objects.requireNonNull(seats, "seats cannot be null");
        Objects.requireNonNull(userId, "userId cannot be null");

        List<ShowSeat> successfullyLocked = new ArrayList<>();
        Instant now = Instant.now();

        // Sort seats by ID to prevent deadlocks when locking multiple resources
        List<ShowSeat> sortedSeats = seats.stream()
                .sorted((a, b) -> a.getShowSeatId().compareTo(b.getShowSeatId()))
                .toList();

        for (ShowSeat seat : sortedSeats) {
            Object lock = seatLocks.computeIfAbsent(seat.getShowSeatId(), k -> new Object());
            synchronized (lock) {
                // If lock expired, reset seat to AVAILABLE
                if (seat.isLockExpired(lockTimeoutSeconds, now)) {
                    seat.unlock();
                }

                if (seat.getStatus() == ShowSeatStatus.AVAILABLE) {
                    seat.lock(userId, now);
                    successfullyLocked.add(seat);
                } else {
                    // Rollback previously locked seats in this batch
                    for (ShowSeat lockedSeat : successfullyLocked) {
                        lockedSeat.unlock();
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public void unlockSeats(List<ShowSeat> seats, String userId) {
        if (seats == null) return;
        for (ShowSeat seat : seats) {
            Object lock = seatLocks.computeIfAbsent(seat.getShowSeatId(), k -> new Object());
            synchronized (lock) {
                if (Objects.equals(seat.getLockedByUserId(), userId)) {
                    seat.unlock();
                }
            }
        }
    }

    public void releaseSeats(List<ShowSeat> seats) {
        if (seats == null) return;
        for (ShowSeat seat : seats) {
            Object lock = seatLocks.computeIfAbsent(seat.getShowSeatId(), k -> new Object());
            synchronized (lock) {
                seat.release();
            }
        }
    }

    public boolean confirmSeats(List<ShowSeat> seats, String userId) {
        Objects.requireNonNull(seats, "seats cannot be null");
        for (ShowSeat seat : seats) {
            Object lock = seatLocks.computeIfAbsent(seat.getShowSeatId(), k -> new Object());
            synchronized (lock) {
                if (!seat.book(userId)) {
                    return false;
                }
            }
        }
        return true;
    }
}
