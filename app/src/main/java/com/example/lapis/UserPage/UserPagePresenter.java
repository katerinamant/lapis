package com.example.lapis.UserPage;

import android.os.Handler;

import org.json.JSONObject;

public class UserPagePresenter {
    private Handler handler;
    private String guestEmail;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public void onPageLoad() {
        UserPageThread userPageThread = new UserPageThread(this.handler, this.guestEmail);
        new Thread(userPageThread).start();
    }

    public void onConfirmRating(JSONObject booking, double rating) {
        NewRatingThread newRatingThread = new NewRatingThread(this.handler, this.guestEmail, booking, rating);
        new Thread(newRatingThread).start();
    }
}
