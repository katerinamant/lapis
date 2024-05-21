package com.example.lapis.RentalPage;

import android.os.Handler;

public class RentalPagePresenter {
    private final Handler handler;
    private final int rentalId;

    public RentalPagePresenter(Handler handler, int rentalId) {
        this.handler = handler;
        this.rentalId = rentalId;
    }

    void onSelectDates(String startDate, String endDate) {
        CheckAvailabilityThread checkAvailabilityThread = new CheckAvailabilityThread(this.handler, this.rentalId, startDate, endDate);
        new Thread(checkAvailabilityThread).start();
    }

    void onConfirmBooking(String guestEmail, String startDate, String endDate) {
        NewBookingThread newBookingThread = new NewBookingThread(this.handler, this.rentalId, guestEmail, startDate, endDate);
        new Thread(newBookingThread).start();
    }
}
