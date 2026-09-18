package com.bookmyshow.pricing;

import com.bookmyshow.enums.SeatType;
import com.bookmyshow.model.Show;

public interface PricingStrategy {
    double calculateSeatPrice(Show show, SeatType seatType);
}
