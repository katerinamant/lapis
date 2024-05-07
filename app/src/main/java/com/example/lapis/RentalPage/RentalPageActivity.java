package com.example.lapis.RentalPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

public class RentalPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_page);

        // Home button
        ImageView headerLogo = findViewById(R.id.rentalpage_header_logo);
        headerLogo.setOnClickListener(view -> goToHomepage());

        MaterialButton checkAvailabilityButton = findViewById(R.id.btn_check_availability);
        checkAvailabilityButton.setOnClickListener(view -> DatePickerDialog());
    }

    private void DatePickerDialog() {
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

            String selectedDateRange = startDate + " - " + endDate;
            Toast.makeText(RentalPageActivity.this, selectedDateRange, Toast.LENGTH_SHORT).show();
        });

        materialDatePicker.addOnNegativeButtonClickListener(view -> materialDatePicker.dismiss());

        // Show the date picker dialog
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void goToHomepage() {
        Intent intent = new Intent(RentalPageActivity.this, HomePageActivity.class);
        startActivity(intent);
    }
}
