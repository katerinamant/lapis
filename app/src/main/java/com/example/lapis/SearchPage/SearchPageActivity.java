package com.example.lapis.SearchPage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.example.lapis.HomePage.HomePageActivity;
import com.example.lapis.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SearchPageActivity extends AppCompatActivity {
    String destinationFilter, datesFilter;
    int capacityFilter;
    double priceFilter, starsFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        capacityFilter = 0;

        // Home button
        ImageView headerLogo = findViewById(R.id.searchpage_header_logo);
        headerLogo.setOnClickListener(view -> {
            // Go to HomePage
            Intent newIntent = new Intent(SearchPageActivity.this, HomePageActivity.class);
            startActivity(newIntent);
        });

        this.destinationHandler(getIntent());
        this.datesHandler();
        this.capacityHandler();
        this.priceHandler();
        this.starsHandler();

        // Search button
        Button searchButton = findViewById(R.id.btn_search);
        searchButton.setOnClickListener(view -> {
            // TODO: Send request and get response
        });
    }

    private void destinationHandler(Intent intent) {
        // Get destination from HomePageActivity
        destinationFilter = intent.getStringExtra("destination");
        // Set destination as text of EditText
        EditText destinationField = findViewById(R.id.searchpage_edit_text_destination);
        destinationField.setText(destinationFilter);
        destinationField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                destinationFilter = destinationField.getText().toString().trim();
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

                datesFilter = String.format("%s - %s", startDate, endDate);
                chooseDatesButton.setTextColor(Color.BLACK);
                chooseDatesButton.setText(datesFilter);
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
                if (capacityFilter > 1) {
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

    private void priceHandler() {
        EditText priceField = findViewById(R.id.searchpage_edit_text_price);
        priceField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                priceFilter = Double.parseDouble(priceField.getText().toString().trim());
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
