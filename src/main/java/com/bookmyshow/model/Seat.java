package com.bookmyshow.model;

import com.bookmyshow.enums.SeatType;
import java.util.Objects;

public class Seat {
    private final String seatId;
    private final String seatNumber;
    private final int rowNum;
    private final int colNum;
    private final SeatType seatType;

    public Seat(String seatId, String seatNumber, int rowNum, int colNum, SeatType seatType) {
        this.seatId = Objects.requireNonNull(seatId, "seatId cannot be null");
        this.seatNumber = Objects.requireNonNull(seatNumber, "seatNumber cannot be null");
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.seatType = Objects.requireNonNull(seatType, "seatType cannot be null");
    }

    public String getSeatId() {
        return seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public int getRowNum() {
        return rowNum;
    }

    public int getColNum() {
        return colNum;
    }

    public SeatType getSeatType() {
        return seatType;
    }
}
