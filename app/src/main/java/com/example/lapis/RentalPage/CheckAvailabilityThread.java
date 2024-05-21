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

public class CheckAvailabilityThread implements Runnable {
    Handler handler;
    int rentalId;
    String startDate, endDate;

    public CheckAvailabilityThread(Handler handler, int rentalId, String startDate, String endDate) {
        this.handler = handler;
        this.rentalId = rentalId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void run() {
        String availability;
        try (Socket requestSocket = new Socket(Utils.SERVER_ADDRESS, Utils.SERVER_PORT);
            DataOutputStream outputStream = new DataOutputStream(requestSocket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(requestSocket.getInputStream())
            ){

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

            // Write to socket
            Utils.clientToServer(outputStream, request.toString());

            // Receive responseString
            String responseString = Utils.serverToClient(inputStream);
            if (responseString == null) {
                Log.d("CheckAvailabilityThread.run()", "Error receiving responseString");
                throw new IOException();
            }

            // Handle JSON input
            JSONObject responseJson = new JSONObject(responseString);
            JSONObject responseBody = new JSONObject(responseJson.getString(Utils.MESSAGE_BODY));
            availability = responseBody.getString(Utils.BODY_FIELD_AVAILABILITY);

            // Close connection
            request = Utils.createRequest(Requests.CLOSE_CONNECTION.name(), "");
            if (request == null) {
                Log.d("CheckAvailabilityThread.run()", "Error creating request");
                throw new RuntimeException();
            }
            Utils.clientToServer(outputStream, request.toString());

        } catch (IOException | JSONException e) {
            Log.d("CheckAvailabilityThread.run()", "Error:\n" + e);
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
