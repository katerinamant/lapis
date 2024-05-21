package com.example.lapis.Utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class Utils {
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);

    // Misc tags
    public static final String MESSAGE_TYPE = "type";
    public static final String MESSAGE_HEADER = "header";
    public static final String MESSAGE_BODY = "body";
    public static final String MESSAGE_TYPE_REQUEST = "request";
    public static final String BODY_FIELD_STATUS = "status";

    // User Authentication
    public static final String BODY_FIELD_GUEST_EMAIL = "guestEmail";
    public static final String BODY_FIELD_GUEST_PASSWORD = "guestPassword";

    // Rental
    public static final String BODY_FIELD_RENTALS = "rentals";
    public static final String BODY_FIELD_FILTERS = "filters";
    public static final String BODY_FIELD_RENTAL_ID = "rentalId";
    public static final String BODY_FIELD_RENTAL_NAME = "rentalName";
    public static final String BODY_FIELD_RENTAL_LOCATION = "rentalLocation";
    public static final String BODY_FIELD_RENTAL_NIGHTLY_RATE = "rentalNightlyRate";
    public static final String BODY_FIELD_RENTAL_CAPACITY = "rentalCapacity";
    public static final String BODY_FIELD_RENTAL_STARS = "rentalStars";
    public static final String BODY_FIELD_RENTAL_STRING = "rentalString";

    // Check availability / booking
    public static final String BODY_FIELD_AVAILABILITY = "availability";
    public static final String BODY_FIELD_START_DATE = "startDate";
    public static final String BODY_FIELD_END_DATE = "endDate";

    // Intent Keys
    public static final String INTENT_KEY_RENTAL_INFO = "rentalInfo";
    public static final String INTENT_KEY_DESTINATION = "destination";

    // Connection
    public static final String SERVER_ADDRESS = "localhost";
    public static final int SERVER_PORT = 8080;

    public static JSONObject createRequest(String header, String body) {
        JSONObject request = new JSONObject();
        try {
            request.put(MESSAGE_TYPE, MESSAGE_TYPE_REQUEST);
            request.put(MESSAGE_HEADER, header);
            if (body.isEmpty()) body = "{}";
            request.put(MESSAGE_BODY, body);
            return request;

        } catch (JSONException e) {
            Log.d("Utils.createRequest()", "Error creating request:\n" + e);
            return null;
        }
    }

    public static void clientToServer(DataOutputStream stream, String msg) throws IOException {
        try {
            stream.writeUTF(msg);
            stream.flush();
        } catch (IOException e) {
           Log.d("Utils.clientToServer()", "Error sending Socket Output:\n" + e);
           throw e;
        }
    }

    public static String serverToClient(DataInputStream stream) {
        try {
            return stream.readUTF();
        } catch (IOException e) {
            Log.d("Utils.serverToClient()", "Error reading Socket Input:\n" + e);
            return null;
        }
    }
}
