package com.example.lapis.HomePage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.lapis.Utils.Requests;
import com.example.lapis.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomePageThread implements Runnable {
    final Handler handler;

    public HomePageThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        JSONArray rentals = Utils.fetchRentalsFromServer(Requests.GET_RENTALS, new JSONObject());

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.BODY_FIELD_RENTALS, rentals.toString());
        msg.setData(bundle);
        this.handler.sendMessage(msg);
    }
}
