package com.bookmyshow.repository;

import com.bookmyshow.model.Show;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryShowRepository implements ShowRepository {
    private final Map<String, Show> storage = new ConcurrentHashMap<>();

    @Override
    public Show save(Show show) {
        Objects.requireNonNull(show, "show cannot be null");
        storage.put(show.getShowId(), show);
        return show;
    }

    @Override
    public Optional<Show> findById(String showId) {
        if (showId == null) return Optional.empty();
        return Optional.ofNullable(storage.get(showId));
    }

    @Override
    public List<Show> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    @Override
    public List<Show> findByMovieId(String movieId) {
        if (movieId == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(s -> s.getMovie() != null && movieId.equals(s.getMovie().getMovieId()))
                .toList();
    }
}
