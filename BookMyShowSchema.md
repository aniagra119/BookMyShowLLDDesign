// ============================================================================
// RELATIONSHIPS & STRUCTURAL CONTRACTS
// ============================================================================
// - BookMyShowPlatform has-a List<City> (Composition)
// - City has-a List<Theatre> (Composition, 1..*)
// - Theatre has-a City (Aggregation)
// - Theatre has-a List<Screen> (Composition, 1..*)
// - Screen has-a Theatre (Aggregation)
// - Screen has-a List<Seat> (Composition, 1..*)
// - Screen has-a Set<Feature> (Composition, Enum: 2D, 3D, DOLBY_IMAX, etc.)
// - Seat has-a SeatType (Composition, Enum: PLATINUM, GOLD, SILVER)
// - Movie has-a Set<Feature> (Composition, supported audio/visual formats)
// - Screen has-a List<Show> (Composition, 1..*)
// - Show has-a Movie (Aggregation)
// - Show has-a Screen (Aggregation)
// - Show has-a Set<Feature> (Composition, features enabled for this show)
// - Show has-a List<ShowSeat> (Composition, 1..*)
// - ShowSeat has-a Show (Aggregation)
// - ShowSeat has-a Seat (Aggregation)
// - ShowSeat has-a SeatType (Composition, Enum)
// - ShowSeat has-a ShowSeatStatus (Composition, Enum: AVAILABLE, LOCKED, BOOKED)
// - User has-a List<Booking> (Composition)
// - Booking has-a User (Aggregation)
// - Booking has-a Show (Aggregation)
// - Booking has-a List<ShowSeat> (Aggregation, multi-seat reservation)
// - Booking has-a BookingStatus (Composition, Enum: PENDING, CONFIRMED, CANCELLED, EXPIRED)
// - Booking has-a Payment (Composition)
// - Payment has-a PaymentMode (Composition, Enum: RAZORPAY_UPI, RAZORPAY_CARD, RAZORPAY_NETBANKING)
// - Payment has-a PaymentStatus (Composition, Enum: SUCCESS, FAILED, REFUNDED)
// - BookingService has-a PricingStrategy (Aggregation via Strategy Pattern)
// - PricingStrategy is interface implemented by DynamicPricingStrategy and BaseSeatPricingStrategy (Strategy Pattern)
// - BookingService has-a PaymentProcessor (Aggregation via Adapter Pattern)
// - PaymentProcessor is interface implemented by RazorpayPaymentAdapter (Adapter Pattern)
// - RazorpayPaymentAdapter has-a ExternalRazorpayClient (Composition via Adapter Pattern)
// - BookingService has-a SeatLockManager (Composition, thread-safe concurrency control)
// - BookingService has-a BookingEventPublisher (Composition via Observer Pattern)
// - BookingEventPublisher has-a List<BookingEventListener> (Aggregation via Observer Pattern)
// - BookingEventListener is interface implemented by TicketNotificationListener and AnalyticsListener (Observer Pattern)

// ============================================================================
// ENUMS
// ============================================================================

enum SeatType {
    PLATINUM,
    GOLD,
    SILVER
}

enum Feature {
    TWO_D,
    THREE_D,
    DOLBY_IMAX,
    DOLBY_VISION,
    FOUR_DX,
    DOLBY_ATMOS
}

enum ShowSeatStatus {
    AVAILABLE,
    LOCKED,
    BOOKED
}

enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    EXPIRED
}

enum PaymentMode {
    RAZORPAY_UPI,
    RAZORPAY_CARD,
    RAZORPAY_NETBANKING
}

enum PaymentStatus {
    SUCCESS,
    FAILED,
    REFUNDED
}

// ============================================================================
// ACTORS & USERS
// ============================================================================

class User {
    - String userId
    - String name
    - String email
    - String phoneNumber

    + User(String userId, String name, String email, String phoneNumber)
    + String getUserId()
    + String getName()
    + String getEmail()
    + String getPhoneNumber()
}

// ============================================================================
// GEOGRAPHY & THEATRE INFRASTRUCTURE
// ============================================================================

class Seat {
    - String seatId
    - String seatNumber
    - int rowNum
    - int colNum
    - SeatType seatType

    + Seat(String seatId, String seatNumber, int rowNum, int colNum, SeatType seatType)
    + String getSeatId()
    + String getSeatNumber()
    + int getRowNum()
    + int getColNum()
    + SeatType getSeatType()
}

class Screen {
    - String screenId
    - String name
    - String theatreId
    - Set<Feature> supportedFeatures
    - List<Seat> seats

    + Screen(String screenId, String name, String theatreId, Set<Feature> supportedFeatures, List<Seat> seats)
    + String getScreenId()
    + String getName()
    + String getTheatreId()
    + Set<Feature> getSupportedFeatures()
    + List<Seat> getSeats()
    + boolean supportsFeature(Feature feature)
    + void addSeat(Seat seat)
}

class Theatre {
    - String theatreId
    - String name
    - String address
    - String cityId
    - List<Screen> screens

    + Theatre(String theatreId, String name, String address, String cityId, List<Screen> screens)
    + String getTheatreId()
    + String getName()
    + String getAddress()
    + String getCityId()
    + List<Screen> getScreens()
    + void addScreen(Screen screen)
}

class City {
    - String cityId
    - String name
    - String state
    - List<Theatre> theatres

    + City(String cityId, String name, String state, List<Theatre> theatres)
    + String getCityId()
    + String getName()
    + String getState()
    + List<Theatre> getTheatres()
    + void addTheatre(Theatre theatre)
}

// ============================================================================
// MOVIES, SHOWS & INVENTORY
// ============================================================================

class Movie {
    - String movieId
    - String title
    - String description
    - int durationMinutes
    - String language
    - Set<Feature> supportedFeatures

    + Movie(String movieId, String title, String description, int durationMinutes, String language, Set<Feature> supportedFeatures)
    + String getMovieId()
    + String getTitle()
    + String getDescription()
    + int getDurationMinutes()
    + String getLanguage()
    + Set<Feature> getSupportedFeatures()
    + boolean supportsFeature(Feature feature)
}

class ShowSeat {
    - String showSeatId
    - String showId
    - Seat seat
    - SeatType seatType
    - ShowSeatStatus status
    - double price
    - String lockedByUserId
    - Instant lockedAt

    + ShowSeat(String showSeatId, String showId, Seat seat, SeatType seatType, double price)
    + String getShowSeatId()
    + String getShowId()
    + Seat getSeat()
    + SeatType getSeatType()
    + ShowSeatStatus getStatus()
    + double getPrice()
    + void setPrice(double price)
    + String getLockedByUserId()
    + Instant getLockedAt()
    + boolean lock(String userId, Instant timestamp)
    + void unlock()
    + void release()
    + boolean book(String userId)
    + boolean isLockExpired(long timeoutSeconds, Instant now)
}

class Show {
    - String showId
    - Movie movie
    - Screen screen
    - LocalDateTime startTime
    - LocalDateTime endTime
    - Set<Feature> showFeatures
    - List<ShowSeat> showSeats

    + Show(String showId, Movie movie, Screen screen, LocalDateTime startTime, LocalDateTime endTime, Set<Feature> showFeatures)
    + String getShowId()
    + Movie getMovie()
    + Screen getScreen()
    + LocalDateTime getStartTime()
    + LocalDateTime getEndTime()
    + Set<Feature> getShowFeatures()
    + List<ShowSeat> getShowSeats()
    + void addShowSeat(ShowSeat showSeat)
    + ShowSeat findShowSeatById(String showSeatId)
}

// ============================================================================
// DYNAMIC PRICING (Strategy Pattern)
// ============================================================================

interface PricingStrategy {
    + double calculateSeatPrice(Show show, SeatType seatType)
}

class DynamicPricingStrategy implements PricingStrategy {
    - double weekendMultiplier
    - double primeTimeMultiplier

    + DynamicPricingStrategy()
    + DynamicPricingStrategy(double weekendMultiplier, double primeTimeMultiplier)
    + double calculateSeatPrice(Show show, SeatType seatType)
}

class BaseSeatPricingStrategy implements PricingStrategy {
    + BaseSeatPricingStrategy()
    + double calculateSeatPrice(Show show, SeatType seatType)
}

// ============================================================================
// CONCURRENCY & SEAT LOCK MANAGER
// ============================================================================

class SeatLockManager {
    - long lockTimeoutSeconds
    - Map<String, Object> seatLocks

    + SeatLockManager()
    + SeatLockManager(long lockTimeoutSeconds)
    + boolean lockSeats(List<ShowSeat> seats, String userId)
    + void unlockSeats(List<ShowSeat> seats, String userId)
    + void releaseSeats(List<ShowSeat> seats)
    + boolean confirmSeats(List<ShowSeat> seats, String userId)
}

// ============================================================================
// BOOKINGS & PAYMENTS (Adapter Pattern)
// ============================================================================

class Payment {
    - String paymentId
    - String bookingId
    - double amount
    - PaymentMode paymentMode
    - PaymentStatus status
    - String transactionReference
    - Instant timestamp

    + Payment(String paymentId, String bookingId, double amount, PaymentMode paymentMode, PaymentStatus status, String transactionReference, Instant timestamp)
    + String getPaymentId()
    + String getBookingId()
    + double getAmount()
    + PaymentMode getPaymentMode()
    + PaymentStatus getStatus()
    + String getTransactionReference()
    + Instant getTimestamp()
}

interface PaymentProcessor {
    + Payment processPayment(String bookingId, double amount, PaymentMode mode)
}

class ExternalRazorpayClient {
    - String keyId
    - String keySecret

    + ExternalRazorpayClient(String keyId, String keySecret)
    + String createOrder(long amountInPaise, String currency, String receipt)
    + String capturePayment(String orderId, long amountInPaise)
    + boolean verifySignature(String orderId, String paymentId, String signature)
}

class RazorpayPaymentAdapter implements PaymentProcessor {
    - ExternalRazorpayClient razorpayClient

    + RazorpayPaymentAdapter(ExternalRazorpayClient razorpayClient)
    + Payment processPayment(String bookingId, double amount, PaymentMode mode)
}

class Booking {
    - String bookingId
    - String bookingNumber
    - User user
    - Show show
    - List<ShowSeat> bookedSeats
    - double totalAmount
    - BookingStatus status
    - Payment payment
    - Instant bookedAt

    + Booking(String bookingId, String bookingNumber, User user, Show show, List<ShowSeat> bookedSeats, double totalAmount)
    + String getBookingId()
    + String getBookingNumber()
    + User getUser()
    + Show getShow()
    + List<ShowSeat> getBookedSeats()
    + double getTotalAmount()
    + BookingStatus getStatus()
    + Payment getPayment()
    + Instant getBookedAt()
    + void confirm(Payment payment)
    + void cancel()
    + void expire()
}

// ============================================================================
// EVENT-DRIVEN NOTIFICATIONS (Observer Pattern)
// ============================================================================

abstract class BookingEvent {
    # String eventId
    # Booking booking
    # Instant timestamp

    + BookingEvent(String eventId, Booking booking, Instant timestamp)
    + String getEventId()
    + Booking getBooking()
    + Instant getTimestamp()
}

class BookingConfirmedEvent extends BookingEvent {
    + BookingConfirmedEvent(String eventId, Booking booking, Instant timestamp)
}

class BookingCancelledEvent extends BookingEvent {
    + BookingCancelledEvent(String eventId, Booking booking, Instant timestamp)
}

interface BookingEventListener {
    + void onBookingConfirmed(BookingConfirmedEvent event)
    + void onBookingCancelled(BookingCancelledEvent event)
}

class TicketNotificationListener implements BookingEventListener {
    + TicketNotificationListener()
    + void onBookingConfirmed(BookingConfirmedEvent event)
    + void onBookingCancelled(BookingCancelledEvent event)
}

class AnalyticsListener implements BookingEventListener {
    + AnalyticsListener()
    + void onBookingConfirmed(BookingConfirmedEvent event)
    + void onBookingCancelled(BookingCancelledEvent event)
    + int getTotalConfirmedBookings()
    + double getTotalGrossRevenue()
}

class BookingEventPublisher {
    - List<BookingEventListener> listeners

    + BookingEventPublisher()
    + void registerListener(BookingEventListener listener)
    + void removeListener(BookingEventListener listener)
    + void publishBookingConfirmed(BookingConfirmedEvent event)
    + void publishBookingCancelled(BookingCancelledEvent event)
}

// ============================================================================
// SERVICES & PLATFORM FACADE
// ============================================================================

class BookingService {
    - SeatLockManager lockManager
    - PricingStrategy pricingStrategy
    - PaymentProcessor paymentProcessor
    - BookingEventPublisher eventPublisher
    - Map<String, Booking> bookings

    + BookingService(SeatLockManager lockManager, PricingStrategy pricingStrategy, PaymentProcessor paymentProcessor, BookingEventPublisher eventPublisher)
    + Booking createBooking(User user, Show show, List<String> showSeatIds)
    + Booking confirmBooking(String bookingId, PaymentMode paymentMode)
    + void cancelBooking(String bookingId)
    + Booking getBooking(String bookingId)
}

class BookMyShowPlatform {
    - Map<String, City> cities
    - Map<String, Movie> movies
    - Map<String, Show> shows
    - Map<String, User> users
    - BookingService bookingService
    - BookingEventPublisher eventPublisher

    + BookMyShowPlatform(BookingService bookingService, BookingEventPublisher eventPublisher)
    + void registerCity(City city)
    + void registerMovie(Movie movie)
    + void registerShow(Show show)
    + void registerUser(User user)
    + List<City> getCities()
    + List<Movie> getMovies()
    + List<Show> getShowsForMovieAndCity(String movieId, String cityId, LocalDate date)
    + Booking bookTickets(String userId, String showId, List<String> seatIds, PaymentMode paymentMode)
    + void cancelBooking(String bookingId)
    + Booking getBooking(String bookingId)
}

// ============================================================================
// DESIGN PATTERN JUSTIFICATION & ARCHITECTURAL SUMMARY
// ============================================================================
// 1. Strategy Pattern (Dynamic Show & Seat Pricing):
//    - PricingStrategy encapsulates price calculation rules (DynamicPricingStrategy vs BaseSeatPricingStrategy).
//    - Computes variable tariffs based on:
//        (a) SeatType (PLATINUM, GOLD, SILVER)
//        (b) Day of week (Weekend Friday-Sunday surge multiplier)
//        (c) Time of day (Prime-time evening 18:00 - 22:00 multiplier)
//        (d) Format features (3D, DOLBY_IMAX, DOLBY_VISION, 4DX premiums)
//      Decouples booking logic from fluctuating ticket rate algorithms complying with OCP.
//
// 2. Adapter Pattern (Razorpay Payment Gateway):
//    - ExternalRazorpayClient is a vendor SDK requiring transaction payloads in paise (1 INR = 100 paise)
//      and proprietary cryptographic signature verification.
//    - RazorpayPaymentAdapter adapts the vendor client to the domain's clean PaymentProcessor interface,
//      shielding core booking transactions from vendor API changes.
//
// 3. Concurrency & Seat Locking (Transactional All-or-Nothing):
//    - SeatLockManager enforces thread-safe temporary locks (10 min TTL) across selected seats.
//    - If any single seat in a multi-seat batch is locked or booked by another user, the entire batch
//      rolls back immediately, guaranteeing zero partial locks and zero double-booking under race conditions.
//
// 4. Observer Pattern (Booking Event Bus):
//    - BookingEventPublisher decouples booking settlement from secondary cross-cutting notifications.
//    - Dispatches BookingConfirmedEvent and BookingCancelledEvent to TicketNotificationListener (SMS/Email
//      QR ticket generation) and AnalyticsListener (theatre box-office gross revenue ledger).
//
// 5. Facade Pattern (BookMyShowPlatform):
//    - Simplifies client interactions across complex subsystem boundaries (City, Theatre, Screen, Movie,
//      Show, SeatLockManager, BookingService, and PaymentProcessor).
//
// 6. Audio/Visual Feature Invariant Enforcement:
//    - Screens, Movies, and Shows maintain Set<Feature> (2D, 3D, DOLBY_IMAX, DOLBY_VISION, 4DX, DOLBY_ATMOS).
//    - Show constructor asserts that every feature requested for the show is supported by both the Screen hardware
//      and the Movie master print.
