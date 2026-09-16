package com.bookmyshow.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class City {
    private final String cityId;
    private final String name;
    private final String state;
    private final List<Theatre> theatres;

    public City(String cityId, String name, String state, List<Theatre> theatres) {
        this.cityId = Objects.requireNonNull(cityId, "cityId cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.state = Objects.requireNonNull(state, "state cannot be null");
        this.theatres = theatres == null ? new ArrayList<>() : new ArrayList<>(theatres);
    }

    public String getCityId() {
        return cityId;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public List<Theatre> getTheatres() {
        return Collections.unmodifiableList(theatres);
    }

    public void addTheatre(Theatre theatre) {
        this.theatres.add(Objects.requireNonNull(theatre, "theatre cannot be null"));
    }
}
