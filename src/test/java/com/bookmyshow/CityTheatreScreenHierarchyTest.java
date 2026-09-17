package com.bookmyshow;

import com.bookmyshow.enums.Feature;
import com.bookmyshow.enums.SeatType;
import com.bookmyshow.model.City;
import com.bookmyshow.model.Movie;
import com.bookmyshow.model.Screen;
import com.bookmyshow.model.Seat;
import com.bookmyshow.model.Show;
import com.bookmyshow.model.Theatre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("City, Theatre, Screen & Feature Compatibility Tests")
class CityTheatreScreenHierarchyTest {

    @Test
    @DisplayName("Should build city hierarchy with theatres, screens, and categorized seats")
    void shouldBuildCityHierarchy() {
        List<Seat> seats = new ArrayList<>();
        seats.add(new Seat("S1", "A1", 1, 1, SeatType.PLATINUM));
        seats.add(new Seat("S2", "A2", 1, 2, SeatType.GOLD));
        seats.add(new Seat("S3", "B1", 2, 1, SeatType.SILVER));

        Screen screen1 = new Screen("SCR-1", "Audi 1", "TH-1", EnumSet.of(Feature.TWO_D, Feature.THREE_D, Feature.DOLBY_ATMOS), seats);
        Screen screen2 = new Screen("SCR-2", "IMAX Screen", "TH-1", EnumSet.of(Feature.DOLBY_IMAX, Feature.THREE_D), seats);

        Theatre theatre = new Theatre("TH-1", "PVR Forum Mall", "Koramangala, Bangalore", "BLR", List.of(screen1, screen2));
        City city = new City("BLR", "Bangalore", "Karnataka", List.of(theatre));

        assertEquals("BLR", city.getCityId());
        assertEquals(1, city.getTheatres().size());
        assertEquals(2, city.getTheatres().get(0).getScreens().size());
        assertEquals(3, screen1.getSeats().size());
        assertTrue(screen1.supportsFeature(Feature.THREE_D));
        assertFalse(screen1.supportsFeature(Feature.DOLBY_IMAX));
    }

    @Test
    @DisplayName("Should throw exception if show requires feature unsupported by Screen")
    void shouldThrowExceptionWhenScreenLacksFeature() {
        Screen screen = new Screen("SCR-1", "Standard Screen", "TH-1", EnumSet.of(Feature.TWO_D), List.of());
        Movie movie = new Movie("MOV-1", "Avatar 3", "Sci-fi", 180, "English", EnumSet.of(Feature.TWO_D, Feature.THREE_D, Feature.DOLBY_IMAX));

        LocalDateTime start = LocalDateTime.of(2026, 9, 20, 18, 0);
        LocalDateTime end = start.plusMinutes(180);

        // Show requires 3D, but screen only supports 2D -> IllegalArgumentException
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Show("SHOW-1", movie, screen, start, end, EnumSet.of(Feature.THREE_D))
        );
        assertTrue(ex.getMessage().contains("does not support required feature"));
    }

    @Test
    @DisplayName("Should throw exception if show requires feature unsupported by Movie")
    void shouldThrowExceptionWhenMovieLacksFeature() {
        Screen screen = new Screen("SCR-1", "IMAX Audi", "TH-1", EnumSet.of(Feature.DOLBY_IMAX, Feature.TWO_D), List.of());
        Movie movie = new Movie("MOV-1", "Classic Drama", "Drama", 120, "Hindi", EnumSet.of(Feature.TWO_D));

        LocalDateTime start = LocalDateTime.of(2026, 9, 20, 18, 0);
        LocalDateTime end = start.plusMinutes(120);

        // Show requires DOLBY_IMAX, but movie only released in 2D
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Show("SHOW-1", movie, screen, start, end, EnumSet.of(Feature.DOLBY_IMAX))
        );
        assertTrue(ex.getMessage().contains("does not support required feature"));
    }
}
