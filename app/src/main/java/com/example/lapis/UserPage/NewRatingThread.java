package com.example.lapis.UserPage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lapis.Utils.Requests;
import com.example.lapis.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NewRatingThread implements Runnable {
    final Handler handler;
    final String guestEmail;
    final JSONObject booking;
    final double rating;

    public NewRatingThread(Handler handler, String guestEmail, JSONObject booking, double rating) {
        this.handler = handler;
        this.guestEmail = guestEmail;
        this.booking = booking;
        this.rating = rating;
    }

    @Override
    public void run() {
        try (Socket requestSocket = new Socket(Utils.SERVER_ADDRESS, Utils.SERVER_PORT);
             DataOutputStream outputStream = new DataOutputStream(requestSocket.getOutputStream());
             DataInputStream inputStream = new DataInputStream(requestSocket.getInputStream())
        ) {

            // Create request
            JSONObject requestBody = new JSONObject();
            try {
                // Create and send request
                requestBody.put(Utils.BODY_FIELD_GUEST_EMAIL, this.guestEmail);
                requestBody.put(Utils.BODY_FIELD_RENTAL_ID, this.booking.get(Utils.BODY_FIELD_RENTAL_ID));
                requestBody.put(Utils.BODY_FIELD_BOOKING_ID, this.booking.get(Utils.BODY_FIELD_BOOKING_ID));
                requestBody.put(Utils.BODY_FIELD_RATING, rating);
            } catch (JSONException e) {
                Log.d("NewRatingThread.run()", "Error creating request body:\n" + e);
                throw new RuntimeException(e);
            }
            JSONObject request = Utils.createRequest(Requests.NEW_RATING.name(), requestBody.toString());
            if (request == null) {
                Log.d("NewRatingThread.run()", "Error creating request");
                throw new RuntimeException();
            }

            // Write to socket
            Utils.clientToServer(outputStream, request.toString());

            // Close connection
            request = Utils.createRequest(Requests.CLOSE_CONNECTION.name(), "");
            if (request == null) {
                Log.d("NewRatingThread.run()", "Error creating request");
                throw new RuntimeException();
            }
            Utils.clientToServer(outputStream, request.toString());

        } catch (IOException e) {
            Log.d("NewRatingThread.run()", "Error:\n" + e);
            throw new RuntimeException(e);
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.BODY_FIELD_STATUS, "OK");
        msg.setData(bundle);
        this.handler.sendMessage(msg);
    }
}
