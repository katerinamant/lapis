package com.example.lapis.SearchPage;

import android.os.Handler;
import android.util.Log;

import com.example.lapis.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchPagePresenter {
    private Handler handler;

    public void setHandler(Handler handler) { this.handler = handler; }

    public void onSearch(String location, String dates, int capacity, double nightlyRate, double stars) {
        JSONObject filters = new JSONObject();
        try {
            filters.put(Utils.BODY_FIELD_RENTAL_LOCATION, location);
            filters.put(Utils.BODY_FIELD_RENTAL_CAPACITY, capacity);
            filters.put(Utils.BODY_FIELD_RENTAL_NIGHTLY_RATE, nightlyRate);
            // TODO: ADD TIME PRIOD FROM FILTERS. USE FILTERS_ENUM FROM BACKEND NOT THESE TAGS
            filters.put(Utils.BODY_FIELD_RENTAL_STARS, stars);
        } catch (JSONException e) {
            Log.d("SearchPagePresenter.onSearch()", "Error inserting user input into filters JSON object");
            throw new RuntimeException(e);
        }
        // SearchPageThread searchPageThread = new SearchPageThread(this.handler);
        // new Thread(searchPageThread).start();
    }
}
