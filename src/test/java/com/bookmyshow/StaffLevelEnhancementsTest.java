package com.bookmyshow;

import com.bookmyshow.enums.BookingStatus;
import com.bookmyshow.enums.Feature;
import com.bookmyshow.enums.PaymentMode;
import com.bookmyshow.enums.SeatType;
import com.bookmyshow.enums.ShowSeatStatus;
import com.bookmyshow.enums.UserRole;
import com.bookmyshow.event.BookingEvent;
import com.bookmyshow.event.BookingEventListener;
import com.bookmyshow.event.BookingEventPublisher;
import com.bookmyshow.lock.SeatLockManager;
import com.bookmyshow.model.Booking;
import com.bookmyshow.model.Movie;
import com.bookmyshow.model.Screen;
import com.bookmyshow.model.Seat;
import com.bookmyshow.model.Show;
import com.bookmyshow.model.ShowSeat;
import com.bookmyshow.model.User;
import com.bookmyshow.payment.adapter.RazorpayPaymentAdapter;
import com.bookmyshow.payment.external.ExternalRazorpayClient;
import com.bookmyshow.pricing.BaseSeatPricingStrategy;
import com.bookmyshow.repository.BookingRepository;
import com.bookmyshow.repository.InMemoryBookingRepository;
import com.bookmyshow.repository.InMemoryShowRepository;
import com.bookmyshow.repository.InMemoryUserRepository;
import com.bookmyshow.repository.ShowRepository;
import com.bookmyshow.repository.UserRepository;
import com.bookmyshow.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class StaffLevelEnhancementsTest {

    private BookingRepository bookingRepository;
    private ShowRepository showRepository;
    private UserRepository userRepository;
    private SeatLockManager lockManager;
    private BookingEventPublisher eventPublisher;
    private BookingService bookingService;
    private User customer;
    private Show show;
    private ShowSeat showSeat;

    @BeforeEach
    void setUp() {
        bookingRepository = new InMemoryBookingRepository();
        showRepository = new InMemoryShowRepository();
        userRepository = new InMemoryUserRepository();
        lockManager = new SeatLockManager(600);
        eventPublisher = new BookingEventPublisher();
        RazorpayPaymentAdapter paymentAdapter = new RazorpayPaymentAdapter(new ExternalRazorpayClient("key", "secret"));
        BaseSeatPricingStrategy pricingStrategy = new BaseSeatPricingStrategy();

        bookingService = new BookingService(
                lockManager,
                pricingStrategy,
                paymentAdapter,
                eventPublisher,
                bookingRepository,
                showRepository,
                userRepository
        );

        customer = new User("USR-1", "Alice", "alice@example.com", "+919876543210", UserRole.CUSTOMER);
        userRepository.save(customer);

        Movie movie = new Movie("MOV-1", "Oppenheimer", "Biographical Drama", 180, "English", EnumSet.of(Feature.DOLBY_IMAX));
        Seat physicalSeat = new Seat("SEAT-1", "A1", 1, 1, SeatType.PLATINUM);
        Screen screen = new Screen("SCR-1", "IMAX Screen 1", "TH-1", EnumSet.of(Feature.DOLBY_IMAX), List.of(physicalSeat));

        show = new Show("SHW-1", movie, screen, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(5), EnumSet.of(Feature.DOLBY_IMAX));
        showSeat = new ShowSeat("SHW-ST-1", "SHW-1", physicalSeat, SeatType.PLATINUM, 300.0);
        show.addShowSeat(showSeat);
        showRepository.save(show);
    }

    @Test
    @DisplayName("UserRole enum validation and role checking")
    void testUserRoles() {
        User admin = new User("USR-ADMIN", "Bob", "bob@theatre.com", "+919876543211", UserRole.THEATRE_ADMIN);
        User platformAdmin = new User("USR-ROOT", "Super", "root@bms.com", "+919876543212", UserRole.PLATFORM_ADMIN);

        assertTrue(customer.hasRole(UserRole.CUSTOMER));
        assertFalse(customer.hasRole(UserRole.THEATRE_ADMIN));

        assertTrue(admin.hasRole(UserRole.THEATRE_ADMIN));
        assertEquals(UserRole.THEATRE_ADMIN, admin.getRole());

        assertTrue(platformAdmin.hasRole(UserRole.PLATFORM_ADMIN));
    }

    @Test
    @DisplayName("ShowSeat finite state machine guards")
    void testShowSeatStateMachineGuards() {
        // Initial state is AVAILABLE
        assertEquals(ShowSeatStatus.AVAILABLE, showSeat.getStatus());
        assertTrue(showSeat.canTransitionTo(ShowSeatStatus.LOCKED, "USR-1"));
        assertFalse(showSeat.canTransitionTo(ShowSeatStatus.BOOKED, "USR-1"));

        // Lock by USR-1
        assertTrue(showSeat.lock("USR-1", Instant.now()));
        assertEquals(ShowSeatStatus.LOCKED, showSeat.getStatus());

        // USR-2 cannot book or lock
        assertFalse(showSeat.canTransitionTo(ShowSeatStatus.BOOKED, "USR-2"));
        assertFalse(showSeat.book("USR-2"));

        // USR-2 cannot unlock seat locked by USR-1
        assertFalse(showSeat.unlock("USR-2"));
        assertEquals(ShowSeatStatus.LOCKED, showSeat.getStatus());

        // USR-1 can book
        assertTrue(showSeat.canTransitionTo(ShowSeatStatus.BOOKED, "USR-1"));
        assertTrue(showSeat.book("USR-1"));
        assertEquals(ShowSeatStatus.BOOKED, showSeat.getStatus());

        // Once BOOKED, cannot transition directly to LOCKED
        assertFalse(showSeat.canTransitionTo(ShowSeatStatus.LOCKED, "USR-1"));
        assertFalse(showSeat.lock("USR-1", Instant.now()));

        // Release transitions back to AVAILABLE
        showSeat.release();
        assertEquals(ShowSeatStatus.AVAILABLE, showSeat.getStatus());
    }

    @Test
    @DisplayName("BookingService coordinates with repositories and generic event listener")
    void testBookingServiceWithRepositoriesAndGenericEventListener() {
        AtomicInteger eventCounter = new AtomicInteger(0);
        List<BookingEvent> receivedEvents = new ArrayList<>();

        eventPublisher.registerListener(new BookingEventListener() {
            @Override
            public void onEvent(BookingEvent event) {
                eventCounter.incrementAndGet();
                receivedEvents.add(event);
            }
        });

        Booking booking = bookingService.createBooking(customer, show, List.of("SHW-ST-1"));
        assertNotNull(booking);
        assertEquals(BookingStatus.PENDING, booking.getStatus());

        // Verify saved in repository
        assertTrue(bookingRepository.findById(booking.getBookingId()).isPresent());
        assertEquals(1, bookingRepository.findByUserId(customer.getUserId()).size());

        // Confirm booking
        Booking confirmed = bookingService.confirmBooking(booking.getBookingId(), PaymentMode.RAZORPAY_UPI);
        assertEquals(BookingStatus.CONFIRMED, confirmed.getStatus());
        assertEquals(1, eventCounter.get());
        assertEquals(confirmed.getBookingId(), receivedEvents.getFirst().getBooking().getBookingId());

        // Verify show and user are present in repos
        assertTrue(showRepository.findById(show.getShowId()).isPresent());
        assertTrue(userRepository.findById(customer.getUserId()).isPresent());
    }
}
