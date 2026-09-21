package com.bookmyshow.service;

import com.bookmyshow.enums.PaymentMode;
import com.bookmyshow.event.BookingEventListener;
import com.bookmyshow.event.BookingEventPublisher;
import com.bookmyshow.model.Booking;
import com.bookmyshow.model.City;
import com.bookmyshow.model.Movie;
import com.bookmyshow.model.Screen;
import com.bookmyshow.model.Show;
import com.bookmyshow.model.Theatre;
import com.bookmyshow.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class BookMyShowPlatform {
    private final Map<String, City> cities = new ConcurrentHashMap<>();
    private final Map<String, Movie> movies = new ConcurrentHashMap<>();
    private final Map<String, Show> shows = new ConcurrentHashMap<>();
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final BookingService bookingService;
    private final BookingEventPublisher eventPublisher;

    public BookMyShowPlatform(BookingService bookingService, BookingEventPublisher eventPublisher) {
        this.bookingService = Objects.requireNonNull(bookingService, "bookingService cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher cannot be null");
    }

    public void registerCity(City city) {
        cities.put(city.getCityId(), city);
    }

    public void registerMovie(Movie movie) {
        movies.put(movie.getMovieId(), movie);
    }

    public void registerShow(Show show) {
        shows.put(show.getShowId(), show);
    }

    public void registerUser(User user) {
        users.put(user.getUserId(), user);
    }

    public void subscribeToEvents(BookingEventListener listener) {
        eventPublisher.registerListener(listener);
    }

    public List<City> getCities() {
        return Collections.unmodifiableList(new ArrayList<>(cities.values()));
    }

    public List<Movie> getMovies() {
        return Collections.unmodifiableList(new ArrayList<>(movies.values()));
    }

    public List<Show> getShowsForMovieAndCity(String movieId, String cityId, LocalDate date) {
        City city = cities.get(cityId);
        if (city == null) {
            return Collections.emptyList();
        }

        List<String> screenIdsInCity = new ArrayList<>();
        for (Theatre theatre : city.getTheatres()) {
            for (Screen screen : theatre.getScreens()) {
                screenIdsInCity.add(screen.getScreenId());
            }
        }

        return shows.values().stream()
                .filter(s -> s.getMovie().getMovieId().equals(movieId))
                .filter(s -> screenIdsInCity.contains(s.getScreen().getScreenId()))
                .filter(s -> s.getStartTime().toLocalDate().equals(date))
                .toList();
    }

    public Booking bookTickets(String userId, String showId, List<String> seatIds, PaymentMode paymentMode) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        Show show = shows.get(showId);
        if (show == null) {
            throw new IllegalArgumentException("Show not found: " + showId);
        }

        Booking booking = bookingService.createBooking(user, show, seatIds);
        return bookingService.confirmBooking(booking.getBookingId(), paymentMode);
    }

    public void cancelBooking(String bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    public Booking getBooking(String bookingId) {
        return bookingService.getBooking(bookingId);
    }
}
