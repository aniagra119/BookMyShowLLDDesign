package com.bookmyshow.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Theatre {
    private final String theatreId;
    private final String name;
    private final String address;
    private final String cityId;
    private final List<Screen> screens;

    public Theatre(String theatreId, String name, String address, String cityId, List<Screen> screens) {
        this.theatreId = Objects.requireNonNull(theatreId, "theatreId cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.address = Objects.requireNonNull(address, "address cannot be null");
        this.cityId = Objects.requireNonNull(cityId, "cityId cannot be null");
        this.screens = screens == null ? new ArrayList<>() : new ArrayList<>(screens);
    }

    public String getTheatreId() {
        return theatreId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCityId() {
        return cityId;
    }

    public List<Screen> getScreens() {
        return Collections.unmodifiableList(screens);
    }

    public void addScreen(Screen screen) {
        this.screens.add(Objects.requireNonNull(screen, "screen cannot be null"));
    }
}
