package com.bookmyshow.event.listener;

import com.bookmyshow.event.BookingCancelledEvent;
import com.bookmyshow.event.BookingConfirmedEvent;
import com.bookmyshow.event.BookingEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TicketNotificationListener implements BookingEventListener {
    private final List<String> notificationsSent = new ArrayList<>();

    @Override
    public void onBookingConfirmed(BookingConfirmedEvent event) {
        String msg = "CONFIRMED: Sent ticket to " + event.getBooking().getUser().getEmail() + " for Booking " + event.getBooking().getBookingNumber();
        notificationsSent.add(msg);
    }

    @Override
    public void onBookingCancelled(BookingCancelledEvent event) {
        String msg = "CANCELLED: Sent cancellation alert to " + event.getBooking().getUser().getEmail() + " for Booking " + event.getBooking().getBookingNumber();
        notificationsSent.add(msg);
    }

    public List<String> getNotificationsSent() {
        return Collections.unmodifiableList(notificationsSent);
    }
}
