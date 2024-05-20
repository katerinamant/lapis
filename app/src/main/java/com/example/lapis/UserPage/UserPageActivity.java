package com.example.lapis.UserPage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

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

public class UserPageActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper(), message -> {
        try {
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

        // Load page and fetch bookings with no ratings
        UserPageViewModel viewModel = new ViewModelProvider(this).get(UserPageViewModel.class);
        viewModel.getPresenter().setHandler(handler);
        viewModel.getPresenter().onPageLoad();

        // Home button
        ImageView headerLogo = findViewById(R.id.userpage_header_logo);
        headerLogo.setOnClickListener(view -> {
            // Go to HomePage
            Intent intent = new Intent(UserPageActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
    }
}
