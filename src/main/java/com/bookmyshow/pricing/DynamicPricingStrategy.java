package com.bookmyshow.pricing;

import com.bookmyshow.enums.Feature;
import com.bookmyshow.enums.SeatType;
import com.bookmyshow.model.Show;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class DynamicPricingStrategy implements PricingStrategy {
    private final double weekendMultiplier;
    private final double primeTimeMultiplier;

    public DynamicPricingStrategy() {
        this(1.3, 1.25);
    }

    public DynamicPricingStrategy(double weekendMultiplier, double primeTimeMultiplier) {
        this.weekendMultiplier = weekendMultiplier;
        this.primeTimeMultiplier = primeTimeMultiplier;
    }

    @Override
    public double calculateSeatPrice(Show show, SeatType seatType) {
        double price = seatType.getBasePrice();

        // 1. Day of Week adjustment (Friday, Saturday, Sunday)
        DayOfWeek dayOfWeek = show.getStartTime().getDayOfWeek();
        if (dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            price *= weekendMultiplier;
        }

        // 2. Time of Day adjustment (Prime Time 18:00 - 22:00)
        LocalTime time = show.getStartTime().toLocalTime();
        if (!time.isBefore(LocalTime.of(18, 0)) && !time.isAfter(LocalTime.of(22, 0))) {
            price *= primeTimeMultiplier;
        }

        // 3. Feature premiums
        for (Feature feature : show.getShowFeatures()) {
            price += getFeatureSurcharge(feature);
        }

        return Math.round(price * 100.0) / 100.0;
    }

    private double getFeatureSurcharge(Feature feature) {
        return switch (feature) {
            case THREE_D -> 50.0;
            case DOLBY_IMAX -> 100.0;
            case DOLBY_VISION -> 80.0;
            case FOUR_DX -> 120.0;
            case DOLBY_ATMOS -> 40.0;
            case TWO_D -> 0.0;
        };
    }
}
