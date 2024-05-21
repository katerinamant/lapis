package com.example.lapis.HomePage;

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

public class HomePageThread implements Runnable {
    Handler handler;

    public HomePageThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        JSONArray rentals = new JSONArray();
        try (Socket requestSocket = new Socket(Utils.SERVER_ADDRESS, Utils.SERVER_PORT);
             DataOutputStream outputStream = new DataOutputStream(requestSocket.getOutputStream());
             DataInputStream inputStream = new DataInputStream(requestSocket.getInputStream())
        ) {

            // Create request
            JSONObject requestBody = new JSONObject();
            JSONObject filters = new JSONObject();
            try {
                // Create and send request
                requestBody.put(Utils.BODY_FIELD_FILTERS, filters);
            } catch (JSONException e) {
                Log.d("HomePageThread.run()", "Error creating request body:\n" + e);
                throw new RuntimeException(e);
            }
            JSONObject request = Utils.createRequest(Requests.GET_RENTALS.name(), requestBody.toString());
            if (request == null) {
                Log.d("HomePageThread.run()", "Error creating request");
                throw new RuntimeException();
            }

            // Write to socket
            Utils.clientToServer(outputStream, request.toString());

            // Receive responseString
            String responseString = Utils.serverToClient(inputStream);
            if (responseString == null) {
                Log.d("HomePageThread.run()", "Error receiving responseString");
                throw new IOException();
            }

            // Handle JSON input
            JSONObject responseJson = new JSONObject(responseString);
            JSONObject responseBody = new JSONObject(responseJson.getString(Utils.MESSAGE_BODY));
            rentals = responseBody.getJSONArray(Utils.BODY_FIELD_RENTALS);

            // Close connection
            request = Utils.createRequest(Requests.CLOSE_CONNECTION.name(), "");
            if (request == null) {
                Log.d("HomePageThread.run()", "Error creating request");
                throw new RuntimeException();
            }
            Utils.clientToServer(outputStream, request.toString());

        } catch (IOException | JSONException e) {
            Log.d("HomePageThread.run()", "Error:\n" + e);
            throw new RuntimeException(e);
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.BODY_FIELD_RENTALS, rentals.toString());
        msg.setData(bundle);
        this.handler.sendMessage(msg);
    }
}
