package com.example.lapis.SearchPage;

import android.os.Handler;
import android.util.Log;

import com.example.lapis.Utils.Filters;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchPagePresenter {
    private Handler handler;

    public void setHandler(Handler handler) { this.handler = handler; }

    public void onPageLoad(String location) {
        JSONObject filters = new JSONObject();
        try {
            if (!location.isEmpty()) {
                filters.put(Filters.LOCATION.name(), location);
            }
        } catch (JSONException e) {
            Log.d("SearchPagePresenter.onPageLoad()", "Error inserting user input into filters JSON object");
            throw new RuntimeException(e);
        }

        SearchPageThread searchPageThread = new SearchPageThread(this.handler, filters);
        new Thread(searchPageThread).start();
    }

    public void onSearch(String location, String dates, int capacity, double nightlyRate, double stars) {
        JSONObject filters = new JSONObject();
        try {
            if (!location.isEmpty()) {
                filters.put(Filters.LOCATION.name(), location);
            }
            if (dates != null) {
                filters.put(Filters.TIME_PERIOD.name(), dates);
            }
            if (capacity > 0) {
                filters.put(Filters.GUESTS.name(), String.valueOf(capacity));
            }
            if (nightlyRate > 0) {
                filters.put(Filters.NIGHTLY_RATE.name(), String.valueOf(nightlyRate));
            }
            if (stars > 0) {
                filters.put(Filters.STARS.name(), String.valueOf(stars));
            }
        } catch (JSONException e) {
            Log.d("SearchPagePresenter.onSearch()", "Error inserting user input into filters JSON object");
            throw new RuntimeException(e);
        }

        SearchPageThread searchPageThread = new SearchPageThread(this.handler, filters);
        new Thread(searchPageThread).start();
    }
}
