package com.bookmyshow.model;

import com.bookmyshow.enums.Feature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Screen {
    private final String screenId;
    private final String name;
    private final String theatreId;
    private final Set<Feature> supportedFeatures;
    private final List<Seat> seats;

    public Screen(String screenId, String name, String theatreId, Set<Feature> supportedFeatures, List<Seat> seats) {
        this.screenId = Objects.requireNonNull(screenId, "screenId cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.theatreId = Objects.requireNonNull(theatreId, "theatreId cannot be null");
        this.supportedFeatures = supportedFeatures == null ? EnumSet.noneOf(Feature.class) : EnumSet.copyOf(supportedFeatures);
        this.seats = seats == null ? new ArrayList<>() : new ArrayList<>(seats);
    }

    public String getScreenId() {
        return screenId;
    }

    public String getName() {
        return name;
    }

    public String getTheatreId() {
        return theatreId;
    }

    public Set<Feature> getSupportedFeatures() {
        return Collections.unmodifiableSet(supportedFeatures);
    }

    public List<Seat> getSeats() {
        return Collections.unmodifiableList(seats);
    }

    public boolean supportsFeature(Feature feature) {
        return supportedFeatures.contains(feature);
    }

    public void addSeat(Seat seat) {
        this.seats.add(Objects.requireNonNull(seat, "seat cannot be null"));
    }
}
