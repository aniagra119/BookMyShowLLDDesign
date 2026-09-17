package com.bookmyshow.model;

import com.bookmyshow.enums.Feature;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Show {
    private final String showId;
    private final Movie movie;
    private final Screen screen;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Set<Feature> showFeatures;
    private final List<ShowSeat> showSeats;

    public Show(String showId, Movie movie, Screen screen, LocalDateTime startTime, LocalDateTime endTime, Set<Feature> showFeatures) {
        this.showId = Objects.requireNonNull(showId, "showId cannot be null");
        this.movie = Objects.requireNonNull(movie, "movie cannot be null");
        this.screen = Objects.requireNonNull(screen, "screen cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "startTime cannot be null");
        this.endTime = Objects.requireNonNull(endTime, "endTime cannot be null");
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("endTime cannot be before startTime");
        }
        this.showFeatures = showFeatures == null ? EnumSet.noneOf(Feature.class) : EnumSet.copyOf(showFeatures);
        this.showSeats = new ArrayList<>();
        validateFeatureCompatibility();
    }

    private void validateFeatureCompatibility() {
        for (Feature feature : showFeatures) {
            if (!screen.supportsFeature(feature)) {
                throw new IllegalArgumentException("Screen " + screen.getName() + " does not support required feature: " + feature);
            }
            if (!movie.supportsFeature(feature)) {
                throw new IllegalArgumentException("Movie " + movie.getTitle() + " does not support required feature: " + feature);
            }
        }
    }

    public String getShowId() {
        return showId;
    }

    public Movie getMovie() {
        return movie;
    }

    public Screen getScreen() {
        return screen;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Set<Feature> getShowFeatures() {
        return Collections.unmodifiableSet(showFeatures);
    }

    public List<ShowSeat> getShowSeats() {
        return Collections.unmodifiableList(showSeats);
    }

    public void addShowSeat(ShowSeat showSeat) {
        this.showSeats.add(Objects.requireNonNull(showSeat, "showSeat cannot be null"));
    }

    public void addShowSeats(List<ShowSeat> seats) {
        this.showSeats.addAll(Objects.requireNonNull(seats, "seats cannot be null"));
    }

    public ShowSeat findShowSeatById(String showSeatId) {
        return showSeats.stream()
                .filter(s -> s.getShowSeatId().equals(showSeatId))
                .findFirst()
                .orElse(null);
    }
}
