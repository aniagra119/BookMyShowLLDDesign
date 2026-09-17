package com.bookmyshow.model;

import com.bookmyshow.enums.Feature;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class Movie {
    private final String movieId;
    private final String title;
    private final String description;
    private final int durationMinutes;
    private final String language;
    private final Set<Feature> supportedFeatures;

    public Movie(String movieId, String title, String description, int durationMinutes, String language, Set<Feature> supportedFeatures) {
        this.movieId = Objects.requireNonNull(movieId, "movieId cannot be null");
        this.title = Objects.requireNonNull(title, "title cannot be null");
        this.description = Objects.requireNonNull(description, "description cannot be null");
        this.durationMinutes = durationMinutes;
        this.language = Objects.requireNonNull(language, "language cannot be null");
        this.supportedFeatures = supportedFeatures == null ? EnumSet.noneOf(Feature.class) : EnumSet.copyOf(supportedFeatures);
    }

    public String getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getLanguage() {
        return language;
    }

    public Set<Feature> getSupportedFeatures() {
        return Collections.unmodifiableSet(supportedFeatures);
    }

    public boolean supportsFeature(Feature feature) {
        return supportedFeatures.contains(feature);
    }
}
