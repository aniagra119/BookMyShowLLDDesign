package com.bookmyshow;

import com.bookmyshow.enums.SeatType;
import com.bookmyshow.enums.ShowSeatStatus;
import com.bookmyshow.lock.SeatLockManager;
import com.bookmyshow.model.Seat;
import com.bookmyshow.model.ShowSeat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Seat Lock Manager & Concurrency Tests")
class SeatLockAndConcurrencyTest {

    @Test
    @DisplayName("Should lock seats successfully and prevent other user from locking same seats")
    void shouldLockSeatsAndPreventCollisions() {
        SeatLockManager lockManager = new SeatLockManager(600);
        Seat seat1 = new Seat("S1", "A1", 1, 1, SeatType.PLATINUM);
        ShowSeat showSeat = new ShowSeat("SS-1", "SHOW-1", seat1, SeatType.PLATINUM, 400.0);

        boolean user1Locked = lockManager.lockSeats(List.of(showSeat), "USER-1");
        assertTrue(user1Locked);
        assertEquals(ShowSeatStatus.LOCKED, showSeat.getStatus());
        assertEquals("USER-1", showSeat.getLockedByUserId());

        // User 2 attempts to lock same seat -> should fail
        boolean user2Locked = lockManager.lockSeats(List.of(showSeat), "USER-2");
        assertFalse(user2Locked);
        assertEquals(ShowSeatStatus.LOCKED, showSeat.getStatus());
        assertEquals("USER-1", showSeat.getLockedByUserId());
    }

    @Test
    @DisplayName("Should rollback partial locks if any seat in batch cannot be locked (Transactional All-or-Nothing)")
    void shouldRollbackIfAnySeatFails() {
        SeatLockManager lockManager = new SeatLockManager(600);
        ShowSeat seatA = new ShowSeat("SS-A", "SHOW-1", new Seat("S1", "A1", 1, 1, SeatType.GOLD), SeatType.GOLD, 250.0);
        ShowSeat seatB = new ShowSeat("SS-B", "SHOW-1", new Seat("S2", "A2", 1, 2, SeatType.GOLD), SeatType.GOLD, 250.0);

        // Pre-lock seatB for User 1
        lockManager.lockSeats(List.of(seatB), "USER-1");

        // User 2 attempts to lock seatA AND seatB
        boolean user2Locked = lockManager.lockSeats(List.of(seatA, seatB), "USER-2");
        assertFalse(user2Locked);

        // seatA must be rolled back to AVAILABLE
        assertEquals(ShowSeatStatus.AVAILABLE, seatA.getStatus());
        assertNull(seatA.getLockedByUserId());
    }

    @Test
    @DisplayName("Should handle 20 concurrent threads trying to book the exact same seat with exactly 1 winner")
    void shouldHandleConcurrentRaceCondition() throws InterruptedException {
        SeatLockManager lockManager = new SeatLockManager(600);
        ShowSeat contestedSeat = new ShowSeat("SS-CONT", "SHOW-1", new Seat("S1", "A1", 1, 1, SeatType.PLATINUM), SeatType.PLATINUM, 400.0);

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final String userId = "USER-" + i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    boolean locked = lockManager.lockSeats(List.of(contestedSeat), userId);
                    if (locked) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception ignored) {
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await();
        executor.shutdown();

        // Exactly one thread must have acquired the lock
        assertEquals(1, successCount.get());
        assertEquals(ShowSeatStatus.LOCKED, contestedSeat.getStatus());
        assertNotNull(contestedSeat.getLockedByUserId());
    }
}
