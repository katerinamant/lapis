package com.example.lapis.RentalPage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lapis.Utils.Requests;
import com.example.lapis.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckAvailabilityThread implements Runnable {
    final Handler handler;
    final int rentalId;
    final String startDate, endDate;

    public CheckAvailabilityThread(Handler handler, int rentalId, String startDate, String endDate) {
        this.handler = handler;
        this.rentalId = rentalId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void run() {
        // Create request
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put(Utils.BODY_FIELD_RENTAL_ID, this.rentalId);
            requestBody.put(Utils.BODY_FIELD_START_DATE, this.startDate);
            requestBody.put(Utils.BODY_FIELD_END_DATE, this.endDate);
        } catch (JSONException e) {
            Log.d("CheckAvailabilityThread.run()", "Error creating request body:\n" + e);
            throw new RuntimeException(e);
        }
        JSONObject request = Utils.createRequest(Requests.CHECK_AVAILABILITY.name(), requestBody.toString());
        if (request == null) {
            Log.d("CheckAvailabilityThread.run()", "Error creating request");
            throw new RuntimeException();
        }

        String responseString = Utils.sendRequestToServer(request);
        if (responseString == null) {
            Log.d("CheckAvailabilityThread.run()", "Error receiving responseString.");
            throw new RuntimeException();
        }

        // Handle JSON input
        String availability;
        try {
            JSONObject responseJson = new JSONObject(responseString);
            JSONObject responseBody = new JSONObject(responseJson.getString(Utils.MESSAGE_BODY));
            availability = responseBody.getString(Utils.BODY_FIELD_AVAILABILITY);
        } catch (JSONException e) {
            Log.d("CheckAvailabilityThread.run()", "Error handling response.");
            throw new RuntimeException(e);
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.BODY_FIELD_AVAILABILITY, availability);
        bundle.putString(Utils.BODY_FIELD_START_DATE, this.startDate);
        bundle.putString(Utils.BODY_FIELD_END_DATE, this.endDate);
        msg.setData(bundle);
        this.handler.sendMessage(msg);
    }
}
