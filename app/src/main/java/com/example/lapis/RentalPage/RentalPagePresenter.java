package com.example.lapis.RentalPage;

import android.os.Handler;

public class RentalPagePresenter {
    private final Handler handler;

    public RentalPagePresenter(Handler handler) {
        this.handler = handler;
    }

    void onSelectDates(int rentalId, String startDate, String endDate) {
        CheckAvailabilityThread checkAvailabilityThread = new CheckAvailabilityThread(this.handler, rentalId, startDate, endDate);
        new Thread(checkAvailabilityThread).start();
    }
}
