package com.example.lapis.UserPage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lapis.Utils.Requests;
import com.example.lapis.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UserPageThread implements Runnable {
    final Handler handler;
    final String guestEmail;

    public UserPageThread(Handler handler, String guestEmail) {
        this.handler = handler;
        this.guestEmail = guestEmail;
    }

    @Override
    public void run() {
        JSONArray bookings = new JSONArray();
        try (Socket requestSocket = new Socket(Utils.SERVER_ADDRESS, Utils.SERVER_PORT);
             DataOutputStream outputStream = new DataOutputStream(requestSocket.getOutputStream());
             DataInputStream inputStream = new DataInputStream(requestSocket.getInputStream())
        ) {

            // Create request
            JSONObject requestBody = new JSONObject();
            try {
                // Create and send request
                requestBody.put(Utils.BODY_FIELD_GUEST_EMAIL, this.guestEmail);
            } catch (JSONException e) {
                Log.d("UserPageThread.run()", "Error creating request body:\n" + e);
                throw new RuntimeException(e);
            }
            JSONObject request = Utils.createRequest(Requests.GET_BOOKINGS_WITH_NO_RATINGS.name(), requestBody.toString());
            if (request == null) {
                Log.d("UserPageThread.run()", "Error creating request");
                throw new RuntimeException();
            }

            // Write to socket
            Utils.clientToServer(outputStream, request.toString());

            // Receive responseString
            String responseString = Utils.serverToClient(inputStream);
            if (responseString == null) {
                Log.d("UserPageThread.run()", "Error receiving responseString");
                throw new IOException();
            }

            // Handle JSON input
            JSONObject responseJson = new JSONObject(responseString);
            JSONObject responseBody = new JSONObject(responseJson.getString(Utils.MESSAGE_BODY));
            bookings = responseBody.getJSONArray(Utils.BODY_FIELD_BOOKINGS);

            // Close connection
            request = Utils.createRequest(Requests.CLOSE_CONNECTION.name(), "");
            if (request == null) {
                Log.d("UserPageThread.run()", "Error creating request");
                throw new RuntimeException();
            }
            Utils.clientToServer(outputStream, request.toString());

        } catch (IOException | JSONException e) {
            Log.d("UserPageThread.run()", "Error:\n" + e);
            throw new RuntimeException(e);
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.BODY_FIELD_BOOKINGS, bookings.toString());
        msg.setData(bundle);
        this.handler.sendMessage(msg);
    }
}
