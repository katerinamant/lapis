package com.example.lapis.HomePage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapis.R;
import com.example.lapis.RentalPage.RentalPageActivity;
import com.example.lapis.SearchPage.SearchPageActivity;
import com.example.lapis.UserPage.UserPageActivity;
import com.example.lapis.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper(), message -> {
        try {
            // Get JSON objects for rentals
            JSONArray rentals = new JSONArray(message.getData().getString(Utils.BODY_FIELD_RENTALS));
            List<JSONObject> rentalList = new ArrayList<>();
            for (int i = 0; i < rentals.length(); i++) {
                try {
                    rentalList.add(rentals.getJSONObject(i));
                } catch (JSONException e) {
                    Log.d("HomePageActivity.Handler()", "Error:\n" + e);
                    throw new RuntimeException(e);
                }
            }

            // Rental Recycler View
            RecyclerView recyclerView = findViewById(R.id.home_page_rental_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new RentalRecyclerViewAdapter(rentalList, this));
        } catch (JSONException e) {
            Log.d("HomePageActivity.Handler()", "Error creating JSONArray from message:\n" + e);
            throw new RuntimeException(e);
        }
        return false;
    });

    public void goToRentalPage(JSONObject rentalInfo) {
        Intent intent = new Intent(HomePageActivity.this, RentalPageActivity.class);
        intent.putExtra(Utils.INTENT_KEY_RENTAL_INFO, rentalInfo.toString());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Load page and fetch suggested rentals
        HomePageViewModel viewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        viewModel.getPresenter().setHandler(handler);
        viewModel.getPresenter().onPageLoad();

        // User button
        ImageView headerLogo = findViewById(R.id.header_user);
        headerLogo.setOnClickListener(view -> {
            // Go to UserPage
            Intent intent = new Intent(HomePageActivity.this, UserPageActivity.class);
            startActivity(intent);
        });

        EditText searchbar = findViewById(R.id.homepage_edit_text_destination);
        searchbar.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                Intent intent = new Intent(HomePageActivity.this, SearchPageActivity.class);

                // Add destination input to intent
                String destination = searchbar.getText().toString().trim();
                intent.putExtra(Utils.INTENT_KEY_DESTINATION, destination);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
