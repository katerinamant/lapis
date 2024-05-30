package com.example.lapis.SearchPage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.lapis.Utils.Requests;
import com.example.lapis.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class SearchPageThread implements Runnable {
    final Handler handler;
    final JSONObject searchFilters;

    public SearchPageThread(Handler handler, JSONObject searchFilters) {
        this.handler = handler;
        this.searchFilters = searchFilters;
    }

    @Override
    public void run() {
        JSONArray rentals = Utils.fetchRentalsFromServer(Requests.GET_RENTALS, searchFilters);

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.BODY_FIELD_RENTALS, rentals.toString());
        msg.setData(bundle);
        this.handler.sendMessage(msg);
    }
}
