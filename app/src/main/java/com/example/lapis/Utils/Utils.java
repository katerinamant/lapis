package com.example.lapis.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.List;

public class Utils {
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);

    // Misc tags
    public static final String MESSAGE_TYPE = "type";
    public static final String MESSAGE_HEADER = "header";
    public static final String MESSAGE_BODY = "body";
    public static final String MESSAGE_TYPE_REQUEST = "request";
    public static final String BODY_FIELD_STATUS = "status";
    public static final String BODY_FIELD_ERROR = "error";

    // User Sign-Up + Authentication
    public static final String BODY_FIELD_GUEST_EMAIL = "guestEmail";
    public static final String BODY_FIELD_GUEST_PASSWORD = "guestPassword";
    public static final String BODY_FIELD_GUEST_FIRST_NAME = "guestFirstName";
    public static final String BODY_FIELD_GUEST_LAST_NAME = "guestLastName";
    public static final String BODY_FIELD_GUEST_PHONE_NUMBER = "guestPhoneNumber";

    // Rental
    public static final String BODY_FIELD_RENTALS = "rentals";
    public static final String BODY_FIELD_FILTERS = "filters";
    public static final String BODY_FIELD_RENTAL_ID = "rentalId";
    public static final String BODY_FIELD_RENTAL_NAME = "rentalName";
    public static final String BODY_FIELD_RENTAL_LOCATION = "rentalLocation";
    public static final String BODY_FIELD_RENTAL_NIGHTLY_RATE = "rentalNightlyRate";
    public static final String BODY_FIELD_RENTAL_CAPACITY = "rentalCapacity";
    public static final String BODY_FIELD_RENTAL_STARS = "rentalStars";
    public static final String BODY_FIELD_RENTAL_RATINGS_NUM = "rentalRatingsNum";
    public static final String BODY_FIELD_RENTAL_STRING = "rentalString";
    public static final String BODY_FIELD_RENTAL_IMAGE_URL = "rentalImageUrl";

    // Check availability / booking
    public static final String BODY_FIELD_AVAILABILITY = "availability";
    public static final String BODY_FIELD_START_DATE = "startDate";
    public static final String BODY_FIELD_END_DATE = "endDate";

    // Booking
    public static final String BODY_FIELD_BOOKING_ID = "bookingId";

    // Bookings with no ratings
    public static final String BODY_FIELD_BOOKINGS = "bookings";
    public static final String BODY_FIELD_BOOKING_DATES_STRING = "bookingDatesString";

    // Rating
    public static final String BODY_FIELD_RATING = "rating";

    // Intent Keys
    public static final String INTENT_KEY_RENTAL_INFO = "rentalInfo";
    public static final String INTENT_KEY_DESTINATION = "destination";

    // Connection
    public static final String SERVER_ADDRESS = "localhost";
    public static final int SERVER_PORT = 8080;

    // Shared preferences
    public static final String SHARED_PREFERENCES = "com.example.lapis";

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

    public static String sendRequestToServer(JSONObject request) {
        try (Socket requestSocket = new Socket(Utils.SERVER_ADDRESS, Utils.SERVER_PORT);
             DataOutputStream outputStream = new DataOutputStream(requestSocket.getOutputStream());
             DataInputStream inputStream = new DataInputStream(requestSocket.getInputStream())
        ){
            // Write to socket
            Utils.clientToServer(outputStream, request.toString());

            // Receive responseString
            String responseString = Utils.serverToClient(inputStream);
            if (responseString == null) {
                Log.d("Utils.sendRequestToServer()", "Error receiving responseString.");
                return null;
            }

            request = Utils.createRequest(Requests.CLOSE_CONNECTION.name(), "");
            if (request == null) {
                Log.d("Utils.sendRequestToServer()", "Error creating request");
                return null;
            }
            Utils.clientToServer(outputStream, request.toString());

            return responseString;
        } catch (IOException e) {
            Log.d("Utils.sendRequestToServer()", "Error:\n" + e);
            return null;
        }
    }

    public static JSONArray fetchRentalsFromServer(Requests requestType, JSONObject searchFilters) {
            // Create request
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put(BODY_FIELD_FILTERS, searchFilters);
            } catch (JSONException e) {
                Log.d("Utils.fetchFromServer()", "Error inserting search filters into JSON request:\n"+ e);
                throw new RuntimeException(e);
            }
            JSONObject request = Utils.createRequest(requestType.name(), requestBody.toString());
            if (request == null) {
                Log.d("Utils.fetchFromServer()", "Error creating request.");
                throw new RuntimeException();
            }

            String responseString = Utils.sendRequestToServer(request);
            if (responseString == null) {
                Log.d("Utils.fetchFromServer()", "Error receiving responseString.");
                throw new RuntimeException();
            }

            // Handle JSON input
            try {
                JSONObject response = new JSONObject(responseString);
                JSONObject responseBody = new JSONObject(response.getString(Utils.MESSAGE_BODY));
                return responseBody.getJSONArray(Utils.BODY_FIELD_RENTALS);
            } catch (JSONException e) {
                Log.d("Utils.fetchFromServer()", "Error handling response.");
                throw new RuntimeException(e);
            }
    }

    public static void jsonArrayToList(JSONArray jsonArray, List<JSONObject> list) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                list.add(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                Log.d("Utils.jsonArrayToList()", "Error:\n" + e);
                throw e;
            }
        }
    }
}
