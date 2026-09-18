package com.bookmyshow;

import com.bookmyshow.enums.Feature;
import com.bookmyshow.enums.SeatType;
import com.bookmyshow.model.Movie;
import com.bookmyshow.model.Screen;
import com.bookmyshow.model.Show;
import com.bookmyshow.pricing.BaseSeatPricingStrategy;
import com.bookmyshow.pricing.DynamicPricingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Dynamic Pricing Strategy Tests")
class DynamicPricingStrategyTest {

    @Test
    @DisplayName("Should return base price for flat pricing strategy")
    void shouldReturnBasePriceForFlatStrategy() {
        BaseSeatPricingStrategy strategy = new BaseSeatPricingStrategy();
        Movie movie = new Movie("M1", "Test", "Test", 100, "Eng", EnumSet.of(Feature.TWO_D));
        Screen screen = new Screen("S1", "Screen 1", "TH-1", EnumSet.of(Feature.TWO_D), List.of());
        Show show = new Show("SH-1", movie, screen, LocalDateTime.of(2026, 9, 21, 14, 0), LocalDateTime.of(2026, 9, 21, 16, 0), EnumSet.of(Feature.TWO_D));

        assertEquals(400.0, strategy.calculateSeatPrice(show, SeatType.PLATINUM));
        assertEquals(250.0, strategy.calculateSeatPrice(show, SeatType.GOLD));
        assertEquals(150.0, strategy.calculateSeatPrice(show, SeatType.SILVER));
    }

    @Test
    @DisplayName("Should apply weekend, prime-time and feature premiums in dynamic strategy")
    void shouldApplyDynamicMultipliersAndPremiums() {
        DynamicPricingStrategy strategy = new DynamicPricingStrategy(1.3, 1.25);
        Movie movie = new Movie("M1", "Inception 2", "SciFi", 150, "Eng", EnumSet.of(Feature.DOLBY_IMAX, Feature.THREE_D));
        Screen screen = new Screen("S1", "IMAX Screen", "TH-1", EnumSet.of(Feature.DOLBY_IMAX, Feature.THREE_D), List.of());

        // Saturday (Weekend 1.3x) at 19:00 (Prime time 1.25x) with 3D (+50) and DOLBY_IMAX (+100)
        LocalDateTime showTime = LocalDateTime.of(2026, 9, 19, 19, 0); // Saturday
        Show show = new Show("SH-1", movie, screen, showTime, showTime.plusMinutes(150), EnumSet.of(Feature.THREE_D, Feature.DOLBY_IMAX));

        // Platinum base = 400.0
        // Price = (400 * 1.3 * 1.25) + 50 (3D) + 100 (IMAX) = (650.0) + 150 = 800.0
        double calculatedPrice = strategy.calculateSeatPrice(show, SeatType.PLATINUM);
        assertEquals(800.0, calculatedPrice);

        // Silver base = 150.0
        // Price = (150 * 1.3 * 1.25) + 150 = 243.75 + 150 = 393.75
        double silverPrice = strategy.calculateSeatPrice(show, SeatType.SILVER);
        assertEquals(393.75, silverPrice);
    }
}
