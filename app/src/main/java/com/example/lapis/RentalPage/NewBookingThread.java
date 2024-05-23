package com.example.lapis.RentalPage;

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

public class NewBookingThread implements Runnable {
    Handler handler;
    int rentalId;
    String guestEmail, startDate, endDate;

    public NewBookingThread(Handler handler, int rentalId, String guestEmail, String startDate, String endDate) {
        this.handler = handler;
        this.rentalId = rentalId;
        this.guestEmail = guestEmail;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void run() {
        String status;
        try (Socket requestSocket = new Socket(Utils.SERVER_ADDRESS, Utils.SERVER_PORT);
             DataOutputStream outputStream = new DataOutputStream(requestSocket.getOutputStream());
             DataInputStream inputStream = new DataInputStream(requestSocket.getInputStream())
        ) {
            // Create request
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put(Utils.BODY_FIELD_RENTAL_ID, this.rentalId);
                requestBody.put(Utils.BODY_FIELD_GUEST_EMAIL, this.guestEmail);
                requestBody.put(Utils.BODY_FIELD_START_DATE, this.startDate);
                requestBody.put(Utils.BODY_FIELD_END_DATE, this.endDate);
            } catch (JSONException e) {
                Log.d("NewBookingThread.run()", "Error creating request body:\n" + e);
                throw new RuntimeException(e);
            }
            JSONObject request = Utils.createRequest(Requests.NEW_BOOKING.name(), requestBody.toString());
            if (request == null) {
                Log.d("NewBookingThread.run()", "Error creating request");
                throw new RuntimeException();
            }

            // Write to socket
            Utils.clientToServer(outputStream, request.toString());

            // Receive responseString
            String responseString = Utils.serverToClient(inputStream);
            if (responseString == null) {
                Log.d("NewBookingThread.run()", "Error receiving responseString");
                throw new IOException();
            }

            // Handle JSON input
            JSONObject responseJson = new JSONObject(responseString);
            JSONObject responseBody = new JSONObject(responseJson.getString(Utils.MESSAGE_BODY));
            status = responseBody.getString(Utils.BODY_FIELD_STATUS);

            // Close connection
            request = Utils.createRequest(Requests.CLOSE_CONNECTION.name(), "");
            if (request == null) {
                Log.d("NewBookingThread.run()", "Error creating request");
                throw new RuntimeException();
            }
            Utils.clientToServer(outputStream, request.toString());

        } catch (IOException | JSONException e) {
            Log.d("NewBookingThread.run()", "Error:\n" + e);
            throw new RuntimeException(e);
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.BODY_FIELD_STATUS, status);
        msg.setData(bundle);
        this.handler.sendMessage(msg);
    }
}
