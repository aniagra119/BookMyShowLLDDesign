package com.bookmyshow.enums;

public enum Feature {
    TWO_D("2D"),
    THREE_D("3D"),
    DOLBY_IMAX("DOLBY_IMAX"),
    DOLBY_VISION("DOLBY_VISION"),
    FOUR_DX("4DX"),
    DOLBY_ATMOS("DOLBY_ATMOS");

    private final String displayName;

    Feature(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
