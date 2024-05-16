package com.example.lapis.LoginPage;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.util.Log;

import com.example.lapis.Utils.Requests;
import com.example.lapis.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LoginThread implements Runnable {
    Handler handler;
    String email, password;

    public LoginThread(Handler handler, String email, String password) {
        this.handler = handler;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run() {
        Socket requestSocket = null;
        String status = "ERROR";
        try {
            requestSocket = new Socket(Utils.SERVER_ADDRESS, Utils.SERVER_PORT);
            DataOutputStream outputStream = new DataOutputStream(requestSocket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(requestSocket.getInputStream());

            // Create request
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put(Utils.BODY_FIELD_GUEST_EMAIL, email);
                requestBody.put(Utils.BODY_FIELD_GUEST_PASSWORD, password);
            } catch (JSONException e) {
                Log.d("LoginThread.run()", "Error creating request body:\n" + e);
                throw new RuntimeException(e);
            }
            JSONObject request = Utils.createRequest(Requests.CHECK_CREDENTIALS.name(), requestBody.toString());
            if (request == null) {
                Log.d("LoginThread.run()", "Error creating request");
                throw new RuntimeException();
            }

            // Write to socket
            Utils.clientToServer(outputStream, request.toString());

            // Receive responseString
            String responseString = Utils.serverToClient(inputStream);
            if (responseString == null) {
                Log.d("LoginThread.run()", "Error receiving responseString");
                throw new IOException();
            }
            // Handle JSON input
            JSONObject responseJson = new JSONObject(responseString);
            JSONObject responseBody = new JSONObject(responseJson.getString(Utils.MESSAGE_BODY));
            status = responseBody.getString(Utils.BODY_FIELD_STATUS);

            request = Utils.createRequest(Requests.CLOSE_CONNECTION.name(), "");
            Utils.clientToServer(outputStream, request.toString());
            inputStream.close();
            outputStream.close();
            requestSocket.close();

        } catch (IOException | JSONException e) {
            Log.d("LoginThread.run()", "Error:\n" + e);
            throw new RuntimeException(e);
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.BODY_FIELD_STATUS, status);
        msg.setData(bundle);
        this.handler.sendMessage(msg);
    }
}
