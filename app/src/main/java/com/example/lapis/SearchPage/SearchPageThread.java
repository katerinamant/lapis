package com.example.lapis.SearchPage;

import android.os.Handler;

import com.example.lapis.Utils.Utils;

import org.json.JSONArray;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SearchPageThread implements Runnable {
    Handler handler;

    public SearchPageThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        Socket requestSocket = null;
        JSONArray rentals;
        try {
            requestSocket = new Socket(Utils.SERVER_ADDRESS, Utils.SERVER_PORT);
            DataOutputStream outputStream = new DataOutputStream(requestSocket.getOutputStream());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
