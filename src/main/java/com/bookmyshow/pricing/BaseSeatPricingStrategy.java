package com.bookmyshow.pricing;

import com.bookmyshow.enums.SeatType;
import com.bookmyshow.model.Show;

public class BaseSeatPricingStrategy implements PricingStrategy {
    @Override
    public double calculateSeatPrice(Show show, SeatType seatType) {
        return seatType.getBasePrice();
    }
}
