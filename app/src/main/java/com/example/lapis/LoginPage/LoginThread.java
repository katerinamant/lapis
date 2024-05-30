package com.example.lapis.LoginPage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lapis.Utils.Requests;
import com.example.lapis.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginThread implements Runnable {
    final Handler handler;
    final String email, password;

    public LoginThread(Handler handler, String email, String password) {
        this.handler = handler;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run() {
        // Create request
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put(Utils.BODY_FIELD_GUEST_EMAIL, this.email);
            requestBody.put(Utils.BODY_FIELD_GUEST_PASSWORD, this.password);
        } catch (JSONException e) {
            Log.d("LoginThread.run()", "Error creating request body:\n" + e);
            throw new RuntimeException(e);
        }
        JSONObject request = Utils.createRequest(Requests.CHECK_CREDENTIALS.name(), requestBody.toString());
        if (request == null) {
            Log.d("LoginThread.run()", "Error creating request.");
            throw new RuntimeException();
        }

        String status;
        String guestEmail = "", guestPhoneNumber = "";
        String responseString = Utils.sendRequestToServer(request);
        if (responseString == null) {
            Log.d("LoginThread.run()", "Error receiving responseString.");
            status = "ERROR";
        } else {
            // Handle JSON input
            try {
                JSONObject responseJson = new JSONObject(responseString);
                JSONObject responseBody = new JSONObject(responseJson.getString(Utils.MESSAGE_BODY));
                status = responseBody.getString(Utils.BODY_FIELD_STATUS);
                if (status.equals("OK")) {
                    guestEmail = responseBody.getString(Utils.BODY_FIELD_GUEST_EMAIL);
                    guestPhoneNumber = responseBody.getString(Utils.BODY_FIELD_GUEST_PHONE_NUMBER);
                }
            } catch (JSONException e) {
                Log.d("LoginThread.run()", "Error handling response.");
                throw new RuntimeException(e);
            }
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.BODY_FIELD_STATUS, status);
        if (status.equals("OK")) {
            bundle.putString(Utils.BODY_FIELD_GUEST_EMAIL, guestEmail);
            bundle.putString(Utils.BODY_FIELD_GUEST_PHONE_NUMBER, guestPhoneNumber);
        }
        msg.setData(bundle);
        this.handler.sendMessage(msg);
    }
}
