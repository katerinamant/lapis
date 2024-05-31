package com.example.lapis.SignUpPage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lapis.Utils.Requests;
import com.example.lapis.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpThread implements Runnable {
    final Handler handler;
    final String email, password, firstName, lastName, phoneNumber;

    public SignUpThread(Handler handler, String email, String password, String firstName, String lastName, String phoneNumber) {
        this.handler = handler;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void run() {
        // Create request
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put(Utils.BODY_FIELD_GUEST_EMAIL, this.email);
            requestBody.put(Utils.BODY_FIELD_GUEST_PASSWORD, this.password);
            requestBody.put(Utils.BODY_FIELD_GUEST_FIRST_NAME, this.firstName);
            requestBody.put(Utils.BODY_FIELD_GUEST_LAST_NAME, this.lastName);
            requestBody.put(Utils.BODY_FIELD_GUEST_PHONE_NUMBER, this.phoneNumber);

            JSONObject request = Utils.createRequest(Requests.SIGN_UP.name(), requestBody.toString());
            if (request == null) {
                Log.d("SignUpThread.run()", "Error creating request.");
                throw new RuntimeException();
            }

            String responseString = Utils.sendRequestToServer(request);
            if (responseString == null) {
                Log.d("SignUpThread.run()", "Error receiving responseString.");
                throw new RuntimeException();
            }

            // Handle JSON input
            JSONObject responseJson = new JSONObject(responseString);
            JSONObject responseBody = new JSONObject(responseJson.getString(Utils.MESSAGE_BODY));
            if (responseBody.has(Utils.BODY_FIELD_ERROR)) {
                // Invalid input
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(Utils.BODY_FIELD_ERROR, responseBody.getString(Utils.BODY_FIELD_ERROR));
                msg.setData(bundle);
                this.handler.sendMessage(msg);
                return;
            }
            // Valid input
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString(Utils.BODY_FIELD_STATUS, responseBody.getString(Utils.BODY_FIELD_STATUS));
            bundle.putString(Utils.BODY_FIELD_GUEST_EMAIL, responseBody.getString(Utils.BODY_FIELD_GUEST_EMAIL));
            bundle.putString(Utils.BODY_FIELD_GUEST_PHONE_NUMBER, responseBody.getString(Utils.BODY_FIELD_GUEST_PHONE_NUMBER));
            bundle.putString(Utils.BODY_FIELD_GUEST_FIRST_NAME, responseBody.getString(Utils.BODY_FIELD_GUEST_FIRST_NAME));
            msg.setData(bundle);
            this.handler.sendMessage(msg);

        } catch (JSONException e) {
            Log.d("SignUpThread.run()", "JSON Exception:\n" + e);
            throw new RuntimeException(e);
        }
    }
}
