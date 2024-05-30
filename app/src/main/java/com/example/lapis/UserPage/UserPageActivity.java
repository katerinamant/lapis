package com.example.lapis.UserPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapis.HomePage.HomePageActivity;
import com.example.lapis.R;
import com.example.lapis.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserPageActivity extends AppCompatActivity implements RatingsRecyclerViewAdapter.ItemSelectionListener {
    RelativeLayout relativeLayout;
    UserPageViewModel viewModel;
    final PopupWindow[] confirmRatingPopup = {null};

    private final Handler handler = new Handler(Looper.getMainLooper(), message -> {
        try {
            if (message.getData().containsKey(Utils.BODY_FIELD_STATUS)) {
                // Message was from NewRatingThread
                UserPageActivity.this.onSuccessfulRating();
                return true;
            }

            // Message was from UserPageTread
            // Get JSON objects for bookings with no ratings
            JSONArray bookings = new JSONArray(message.getData().getString(Utils.BODY_FIELD_BOOKINGS));
            List<JSONObject> bookingList = new ArrayList<>();
            try {
                Utils.jsonArrayToList(bookings, bookingList);
            } catch (JSONException e) {
                Log.d("UserPageActivity.Handler()", "Error:\n" + e);
                throw new RuntimeException(e);
            }

            // Ratings Recycler View
            RecyclerView recyclerView = findViewById(R.id.user_page_ratings_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new RatingsRecyclerViewAdapter(bookingList, this));
        } catch (JSONException e) {
            Log.d("UserPageActivity.Handler()", "Error creating JSONArray from message:\n" + e);
            throw new RuntimeException(e);
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // Display user information
        SharedPreferences sharedPreferences = this.getSharedPreferences(Utils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        TextView guestEmailText = findViewById(R.id.user_email);
        String guestEmail = sharedPreferences.getString(Utils.BODY_FIELD_GUEST_EMAIL, getResources().getString(R.string.na));
        guestEmailText.setText(guestEmail);
        TextView guestPhoneNumberText = findViewById(R.id.user_phone_number);
        String guestPhoneNumber = sharedPreferences.getString(Utils.BODY_FIELD_GUEST_PHONE_NUMBER, getResources().getString(R.string.na));
        guestPhoneNumberText.setText(guestPhoneNumber);

        relativeLayout = findViewById(R.id.relative_user_page);

        // Load page and fetch bookings with no ratings
        viewModel = new ViewModelProvider(this).get(UserPageViewModel.class);
        viewModel.getPresenter().setHandler(handler);
        viewModel.getPresenter().setGuestEmail(guestEmail);
        viewModel.getPresenter().onPageLoad();

        // Home button
        ImageView headerLogo = findViewById(R.id.userpage_header_logo);
        headerLogo.setOnClickListener(view -> {
            // Go to HomePage
            Intent intent = new Intent(UserPageActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
    }

    private void onSuccessfulRating() {
        // Restart activity with an updated stays list
        if (confirmRatingPopup[0] != null) {
            confirmRatingPopup[0].dismiss();
            confirmRatingPopup[0] = null;
        }
        finish();
        startActivity(getIntent());
    }

    // ItemSelectionListener implementations
    @SuppressLint("DefaultLocale")
    @Override
    public void onRating(JSONObject booking, double rating) {
        // Inflate popup layout
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View pop_up = layoutInflater.inflate(R.layout.popup_confirm_rating, null);

        // Create and show confirm rating popup
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        // The array is used because
        // the variable needs to be final
        // for the onClickListener
        confirmRatingPopup[0] = new PopupWindow(pop_up, width, height, true);
        confirmRatingPopup[0].showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);

        // Fill popup information
        String rentalName, rentalLocation;
        try {
            rentalName = booking.getString(Utils.BODY_FIELD_RENTAL_NAME);
            rentalLocation = booking.getString(Utils.BODY_FIELD_RENTAL_LOCATION);
        } catch (JSONException e) {
            Log.d("UserPageActivity.onRating()", "Error:\n" + e);
            throw new RuntimeException(e);
        }
        TextView rentalNameText = pop_up.findViewById(R.id.popup_rental_name);
        rentalNameText.setText(rentalName);
        TextView rentalLocationText = pop_up.findViewById(R.id.popup_rental_location);
        rentalLocationText.setText(rentalLocation);
        TextView ratingText = pop_up.findViewById(R.id.popup_rating);
        ratingText.setText(String.format("%.1f", rating));

        Button cancelButton = pop_up.findViewById(R.id.popup_cancel_btn);
        cancelButton.setOnClickListener(v -> {
            confirmRatingPopup[0].dismiss();
            confirmRatingPopup[0] = null;
        });

        Button confirmButton = pop_up.findViewById(R.id.popup_confirm_btn);
        confirmButton.setOnClickListener(v -> viewModel.getPresenter().onConfirmRating(booking, rating));
    }
}
