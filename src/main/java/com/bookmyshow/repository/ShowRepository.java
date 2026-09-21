package com.bookmyshow.repository;

import com.bookmyshow.model.Show;
import java.util.List;
import java.util.Optional;

public interface ShowRepository {
    Show save(Show show);
    Optional<Show> findById(String showId);
    List<Show> findAll();
    List<Show> findByMovieId(String movieId);
}
