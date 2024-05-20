package com.example.lapis.UserPage;

import android.os.Handler;

import org.json.JSONObject;

public class UserPagePresenter {
    private Handler handler;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void onPageLoad() {
        UserPageThread userPageThread = new UserPageThread(this.handler);
        new Thread(userPageThread).start();
    }

    public void onConfirmRating(JSONObject booking, double rating) {
        NewRatingThread newRatingThread = new NewRatingThread(this.handler, booking, rating);
        new Thread(newRatingThread).start();
    }
}
