package com.bookmyshow.service;

import com.bookmyshow.enums.BookingStatus;
import com.bookmyshow.enums.PaymentMode;
import com.bookmyshow.event.BookingCancelledEvent;
import com.bookmyshow.event.BookingConfirmedEvent;
import com.bookmyshow.event.BookingEventPublisher;
import com.bookmyshow.lock.SeatLockManager;
import com.bookmyshow.model.Booking;
import com.bookmyshow.model.Payment;
import com.bookmyshow.model.Show;
import com.bookmyshow.model.ShowSeat;
import com.bookmyshow.model.User;
import com.bookmyshow.payment.PaymentProcessor;
import com.bookmyshow.pricing.PricingStrategy;
import com.bookmyshow.repository.BookingRepository;
import com.bookmyshow.repository.InMemoryBookingRepository;
import com.bookmyshow.repository.InMemoryShowRepository;
import com.bookmyshow.repository.InMemoryUserRepository;
import com.bookmyshow.repository.ShowRepository;
import com.bookmyshow.repository.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BookingService {
    private final SeatLockManager lockManager;
    private final PricingStrategy pricingStrategy;
    private final PaymentProcessor paymentProcessor;
    private final BookingEventPublisher eventPublisher;
    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;

    public BookingService(SeatLockManager lockManager, PricingStrategy pricingStrategy, PaymentProcessor paymentProcessor, BookingEventPublisher eventPublisher) {
        this(lockManager, pricingStrategy, paymentProcessor, eventPublisher,
             new InMemoryBookingRepository(), new InMemoryShowRepository(), new InMemoryUserRepository());
    }

    public BookingService(SeatLockManager lockManager,
                          PricingStrategy pricingStrategy,
                          PaymentProcessor paymentProcessor,
                          BookingEventPublisher eventPublisher,
                          BookingRepository bookingRepository,
                          ShowRepository showRepository,
                          UserRepository userRepository) {
        this.lockManager = Objects.requireNonNull(lockManager, "lockManager cannot be null");
        this.pricingStrategy = Objects.requireNonNull(pricingStrategy, "pricingStrategy cannot be null");
        this.paymentProcessor = Objects.requireNonNull(paymentProcessor, "paymentProcessor cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher cannot be null");
        this.bookingRepository = Objects.requireNonNull(bookingRepository, "bookingRepository cannot be null");
        this.showRepository = Objects.requireNonNull(showRepository, "showRepository cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository cannot be null");
    }

    public Booking createBooking(User user, Show show, List<String> showSeatIds) {
        Objects.requireNonNull(user, "user cannot be null");
        Objects.requireNonNull(show, "show cannot be null");
        if (showSeatIds == null || showSeatIds.isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected");
        }

        // Ensure user and show are registered in repositories
        userRepository.save(user);
        showRepository.save(show);

        List<ShowSeat> selectedSeats = new ArrayList<>();
        for (String seatId : showSeatIds) {
            ShowSeat seat = show.findShowSeatById(seatId);
            if (seat == null) {
                throw new IllegalArgumentException("Seat " + seatId + " does not belong to show " + show.getShowId());
            }
            selectedSeats.add(seat);
        }

        // 1. Transactional atomic locking of seats
        boolean locked = lockManager.lockSeats(selectedSeats, user.getUserId());
        if (!locked) {
            throw new IllegalStateException("One or more selected seats are not available or currently locked by another user");
        }

        // 2. Compute dynamic price for each seat
        double totalAmount = 0.0;
        for (ShowSeat seat : selectedSeats) {
            double price = pricingStrategy.calculateSeatPrice(show, seat.getSeatType());
            seat.setPrice(price);
            totalAmount += price;
        }

        String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 8);
        String bookingNumber = "BMS-" + System.currentTimeMillis();
        Booking booking = new Booking(bookingId, bookingNumber, user, show, selectedSeats, totalAmount);
        bookingRepository.save(booking);
        return booking;
    }

    public Booking confirmBooking(String bookingId, PaymentMode paymentMode) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Booking is not in PENDING status: " + booking.getStatus());
        }

        // 1. Process payment via adapter
        Payment payment = paymentProcessor.processPayment(bookingId, booking.getTotalAmount(), paymentMode);

        // 2. Confirm seats in lock manager
        boolean confirmed = lockManager.confirmSeats(booking.getBookedSeats(), booking.getUser().getUserId());
        if (!confirmed) {
            throw new IllegalStateException("Failed to confirm seat lock for booking " + bookingId);
        }

        // 3. Mark booking confirmed
        booking.confirm(payment);
        bookingRepository.save(booking);

        // 4. Publish observer event
        eventPublisher.publishBookingConfirmed(new BookingConfirmedEvent("EVT-" + UUID.randomUUID(), booking, Instant.now()));
        return booking;
    }

    public void cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        lockManager.releaseSeats(booking.getBookedSeats());
        booking.cancel();
        bookingRepository.save(booking);

        eventPublisher.publishBookingCancelled(new BookingCancelledEvent("EVT-" + UUID.randomUUID(), booking, Instant.now()));
    }

    public Booking getBooking(String bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    public BookingRepository getBookingRepository() {
        return bookingRepository;
    }

    public ShowRepository getShowRepository() {
        return showRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
