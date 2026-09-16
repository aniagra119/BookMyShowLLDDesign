package com.bookmyshow.enums;

public enum SeatType {
    PLATINUM(400.0),
    GOLD(250.0),
    SILVER(150.0);

    private final double basePrice;

    SeatType(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getBasePrice() {
        return basePrice;
    }
}
