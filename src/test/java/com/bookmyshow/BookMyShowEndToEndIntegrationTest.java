package com.bookmyshow;

import com.bookmyshow.enums.BookingStatus;
import com.bookmyshow.enums.Feature;
import com.bookmyshow.enums.PaymentMode;
import com.bookmyshow.enums.SeatType;
import com.bookmyshow.enums.ShowSeatStatus;
import com.bookmyshow.event.BookingEventPublisher;
import com.bookmyshow.event.listener.AnalyticsListener;
import com.bookmyshow.event.listener.TicketNotificationListener;
import com.bookmyshow.lock.SeatLockManager;
import com.bookmyshow.model.Booking;
import com.bookmyshow.model.City;
import com.bookmyshow.model.Movie;
import com.bookmyshow.model.Screen;
import com.bookmyshow.model.Seat;
import com.bookmyshow.model.Show;
import com.bookmyshow.model.ShowSeat;
import com.bookmyshow.model.Theatre;
import com.bookmyshow.model.User;
import com.bookmyshow.payment.adapter.RazorpayPaymentAdapter;
import com.bookmyshow.payment.external.ExternalRazorpayClient;
import com.bookmyshow.pricing.DynamicPricingStrategy;
import com.bookmyshow.service.BookMyShowPlatform;
import com.bookmyshow.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookMyShow End-to-End Integration Tests")
class BookMyShowEndToEndIntegrationTest {

    private BookMyShowPlatform platform;
    private TicketNotificationListener notificationListener;
    private AnalyticsListener analyticsListener;
    private User testUser;
    private Show testShow;
    private ShowSeat seat1;
    private ShowSeat seat2;

    @BeforeEach
    void setUp() {
        SeatLockManager lockManager = new SeatLockManager(600);
        DynamicPricingStrategy pricingStrategy = new DynamicPricingStrategy(1.0, 1.0); // Flat multipliers for test predictability
        ExternalRazorpayClient razorpayClient = new ExternalRazorpayClient("rzp_live_key", "rzp_secret");
        RazorpayPaymentAdapter paymentAdapter = new RazorpayPaymentAdapter(razorpayClient);
        BookingEventPublisher eventPublisher = new BookingEventPublisher();

        notificationListener = new TicketNotificationListener();
        analyticsListener = new AnalyticsListener();
        eventPublisher.registerListener(notificationListener);
        eventPublisher.registerListener(analyticsListener);

        BookingService bookingService = new BookingService(lockManager, pricingStrategy, paymentAdapter, eventPublisher);
        platform = new BookMyShowPlatform(bookingService, eventPublisher);

        // Setup City, Theatre, Screen, Movie, Show
        Seat s1 = new Seat("SEAT-1", "A1", 1, 1, SeatType.PLATINUM);
        Seat s2 = new Seat("SEAT-2", "A2", 1, 2, SeatType.GOLD);
        Screen screen = new Screen("SCR-1", "Audi 1", "TH-1", EnumSet.of(Feature.TWO_D, Feature.DOLBY_ATMOS), List.of(s1, s2));
        Theatre theatre = new Theatre("TH-1", "PVR Orion", "Rajajinagar, Bangalore", "BLR", List.of(screen));
        City bangalore = new City("BLR", "Bangalore", "Karnataka", List.of(theatre));

        Movie movie = new Movie("MOV-1", "Oppenheimer", "Biographical drama", 180, "English", EnumSet.of(Feature.TWO_D, Feature.DOLBY_ATMOS));
        LocalDateTime showTime = LocalDateTime.of(2026, 9, 21, 18, 0);
        testShow = new Show("SHOW-1", movie, screen, showTime, showTime.plusMinutes(180), EnumSet.of(Feature.TWO_D, Feature.DOLBY_ATMOS));

        seat1 = new ShowSeat("SS-1", testShow.getShowId(), s1, SeatType.PLATINUM, 400.0);
        seat2 = new ShowSeat("SS-2", testShow.getShowId(), s2, SeatType.GOLD, 250.0);
        testShow.addShowSeat(seat1);
        testShow.addShowSeat(seat2);

        testUser = new User("USR-1", "Anirudh Agrawal", "anirudh@example.com", "+919876543210");

        platform.registerCity(bangalore);
        platform.registerMovie(movie);
        platform.registerShow(testShow);
        platform.registerUser(testUser);
    }

    @Test
    @DisplayName("Should query shows in city by movie and date")
    void shouldFindShowsInCity() {
        List<Show> shows = platform.getShowsForMovieAndCity("MOV-1", "BLR", LocalDate.of(2026, 9, 21));
        assertEquals(1, shows.size());
        assertEquals("SHOW-1", shows.get(0).getShowId());
    }

    @Test
    @DisplayName("Should execute complete booking flow: lock, price, Razorpay payment, seat confirmation, and observer notifications")
    void shouldExecuteCompleteBookingFlow() {
        Booking booking = platform.bookTickets("USR-1", "SHOW-1", List.of("SS-1", "SS-2"), PaymentMode.RAZORPAY_UPI);

        assertNotNull(booking);
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(2, booking.getBookedSeats().size());

        // Check seats are BOOKED
        assertEquals(ShowSeatStatus.BOOKED, seat1.getStatus());
        assertEquals(ShowSeatStatus.BOOKED, seat2.getStatus());

        // Check pricing: Platinum (400 + 40 Atmos) + Gold (250 + 40 Atmos) = 440 + 290 = 730
        assertEquals(730.0, booking.getTotalAmount());

        // Check payment record
        assertNotNull(booking.getPayment());
        assertTrue(booking.getPayment().getTransactionReference().startsWith("pay_rzp_"));

        // Check Observer notification was dispatched
        assertEquals(1, notificationListener.getNotificationsSent().size());
        assertTrue(notificationListener.getNotificationsSent().get(0).contains("CONFIRMED"));

        // Check Analytics ledger
        assertEquals(1, analyticsListener.getTotalConfirmedBookings());
        assertEquals(730.0, analyticsListener.getTotalGrossRevenue());
    }

    @Test
    @DisplayName("Should cancel booking, release seats, and publish cancellation event")
    void shouldCancelBookingAndReleaseSeats() {
        Booking booking = platform.bookTickets("USR-1", "SHOW-1", List.of("SS-1"), PaymentMode.RAZORPAY_CARD);
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        platform.cancelBooking(booking.getBookingId());

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(ShowSeatStatus.AVAILABLE, seat1.getStatus());
        assertNull(seat1.getLockedByUserId());

        // Check cancellation notification was dispatched
        assertEquals(2, notificationListener.getNotificationsSent().size()); // 1 confirmed, 1 cancelled
        assertTrue(notificationListener.getNotificationsSent().get(1).contains("CANCELLED"));
        assertEquals(1, analyticsListener.getTotalCancelledBookings());
    }
}
