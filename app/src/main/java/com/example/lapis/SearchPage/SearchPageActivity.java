package com.example.lapis.SearchPage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapis.HomePage.HomePageActivity;
import com.example.lapis.R;
import com.example.lapis.RentalPage.RentalPageActivity;
import com.example.lapis.RentalRecyclerView.RecyclerViewActivity;
import com.example.lapis.RentalRecyclerView.RentalRecyclerViewAdapter;
import com.example.lapis.Utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchPageActivity extends AppCompatActivity implements RecyclerViewActivity {
    String guestEmail, locationFilter, datesFilter;
    int capacityFilter;
    double nightlyRateFilter, starsFilter;

    private final Handler handler = new Handler(Looper.getMainLooper(), message -> {
        try {
            // Get JSON object for rentals
            JSONArray rentals = new JSONArray(message.getData().getString(Utils.BODY_FIELD_RENTALS));
            List<JSONObject> rentalList = new ArrayList<>();
            try {
                Utils.jsonArrayToList(rentals, rentalList);
            } catch (JSONException e) {
                Log.d("SearchPageActivity.Handler()", "Error:\n" + e);
                throw new RuntimeException(e);
            }

            // Rental Recycler View
            RecyclerView recyclerView = findViewById(R.id.search_page_rental_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new RentalRecyclerViewAdapter(rentalList, this));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return false;
    });

    @Override
    public void goToRentalPage(JSONObject rentalInfo) {
        Intent intent = new Intent(SearchPageActivity.this, RentalPageActivity.class);
        intent.putExtra(Utils.INTENT_KEY_RENTAL_INFO, rentalInfo.toString());
        intent.putExtra(Utils.BODY_FIELD_GUEST_EMAIL, guestEmail);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        // Get destination from HomePageActivity
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            locationFilter = intent.getStringExtra(Utils.INTENT_KEY_DESTINATION);
            guestEmail = intent.getStringExtra(Utils.INTENT_KEY_DESTINATION);
        }

        // Instantiate view model
        SearchPageViewModel viewModel = new ViewModelProvider(this).get(SearchPageViewModel.class);
        viewModel.getPresenter().setHandler(handler);

        // Execute initial search with destination
        viewModel.getPresenter().onPageLoad(locationFilter);

        // Home button
        ImageView headerLogo = findViewById(R.id.searchpage_header_logo);
        headerLogo.setOnClickListener(view -> {
            // Go to HomePage
            Intent newIntent = new Intent(SearchPageActivity.this, HomePageActivity.class);
            startActivity(newIntent);
        });

        this.destinationHandler();
        this.datesHandler();
        this.capacityHandler();
        this.nightlyRateHandler();
        this.starsHandler();

        // Search button
        Button searchButton = findViewById(R.id.btn_search);
        searchButton.setOnClickListener(view ->
                viewModel.getPresenter().onSearch(locationFilter, datesFilter, capacityFilter, nightlyRateFilter, starsFilter)
        );

        // Clear filters button
        ImageView clearFiltersIcon = findViewById(R.id.clear_filters_icon);
        clearFiltersIcon.setOnClickListener(view -> {
            // Empty destination field
            EditText destinationField = findViewById(R.id.searchpage_edit_text_destination);
            destinationField.setText("");
            locationFilter = "";

            // Empty "Choose dates" button text
            MaterialButton chooseDatesButton = findViewById(R.id.btn_choose_dates);
            chooseDatesButton.setTextColor(ContextCompat.getColor(SearchPageActivity.this, R.color.hint));
            chooseDatesButton.setText(getResources().getString(R.string.choose_dates));
            datesFilter = "";

            // Empty capacity text
            TextView capacityText = findViewById(R.id.capacity_text);
            capacityText.setTextColor(ContextCompat.getColor(SearchPageActivity.this, R.color.hint));
            capacityText.setText(getResources().getString(R.string.capacity));
            capacityFilter = 0;

            // Empty nightly rate field
            EditText nightlyRateField = findViewById(R.id.searchpage_edit_text_price);
            nightlyRateField.setText("");
            nightlyRateFilter = 0;

            // Empty rating bar
            RatingBar ratingBar = findViewById(R.id.searchpage_rating_bar);
            ratingBar.setRating(0.0f);

            // Execute search with no filters
            viewModel.getPresenter().onPageLoad(locationFilter);
        });
    }

    private void destinationHandler() {
        // Set destination as text of EditText
        EditText destinationField = findViewById(R.id.searchpage_edit_text_destination);
        destinationField.setText(locationFilter);
        destinationField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locationFilter = destinationField.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void datesHandler() {
        // Show calendar when user
        // clicks on "Choose dates" button
        MaterialButton chooseDatesButton = findViewById(R.id.btn_choose_dates);
        chooseDatesButton.setOnClickListener(view -> {
            // Create calendar constraints to only allow future days
            CalendarConstraints calendarConstraints = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build();

            // Create builder
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setCalendarConstraints(calendarConstraints);
            builder.setTheme(R.style.ThemeOverlay_App_MaterialCalendar);
            builder.setTitleText("Select dates:");

            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                // Retrieve the selected start and end dates
                String startDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection.first));
                String endDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection.second));

                datesFilter = String.format("%s-%s", startDate, endDate);
                chooseDatesButton.setTextColor(Color.BLACK);
                chooseDatesButton.setText(String.format("%s - %s", startDate, endDate));
            });

            materialDatePicker.addOnNegativeButtonClickListener(x -> materialDatePicker.dismiss());

            // Show the date picker dialog
            materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });
    }

    private void capacityHandler() {
        // Modify capacity text
        // on plus and minus
        TextView capacityText = findViewById(R.id.capacity_text);
        TextView capacityMinus = findViewById(R.id.capacity_minus);
        capacityMinus.setOnClickListener(v -> {
            if (capacityFilter >= 1) {
                capacityFilter--;
                if (capacityFilter >= 1) {
                    capacityText.setTextColor(Color.BLACK);
                    capacityText.setText(String.valueOf(capacityFilter));
                } else {
                    // capacity = 0
                    // There is no capacity restriction
                    capacityText.setTextColor(ContextCompat.getColor(SearchPageActivity.this, R.color.hint));
                    capacityText.setText(getResources().getString(R.string.capacity));
                }
            }
        });
        TextView capacityPlus = findViewById(R.id.capacity_plus);
        capacityPlus.setOnClickListener(v -> {
            capacityFilter++;
            capacityText.setTextColor(Color.BLACK);
            capacityText.setText(String.valueOf(capacityFilter));
        });
    }

    private void nightlyRateHandler() {
        EditText nightlyRateField = findViewById(R.id.searchpage_edit_text_price);
        nightlyRateField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nightlyRateInput = nightlyRateField.getText().toString().trim();
                if (nightlyRateInput.isEmpty()) {
                    nightlyRateFilter = 0;
                } else {
                    nightlyRateFilter = Double.parseDouble(nightlyRateInput);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void starsHandler() {
        RatingBar ratingBar = findViewById(R.id.searchpage_rating_bar);

        ratingBar.setOnRatingBarChangeListener((arg0, rateValue, arg2) -> starsFilter = rateValue);
    }
}
