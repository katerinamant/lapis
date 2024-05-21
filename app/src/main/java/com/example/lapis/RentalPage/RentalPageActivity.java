package com.example.lapis.RentalPage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.lapis.HomePage.HomePageActivity;
import com.example.lapis.R;
import com.example.lapis.Utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RentalPageActivity extends AppCompatActivity implements RentalPageView {
    String rentalName;
    private final Handler handler = new Handler(Looper.getMainLooper(), message -> {
        if (message.getData().containsKey(Utils.BODY_FIELD_AVAILABILITY)) {
            // Message was from CheckAvailabilityThread
            String availability = message.getData().getString(Utils.BODY_FIELD_AVAILABILITY);
            assert availability != null;
            if (availability.equals("AVAILABLE")) {
                String startDate = message.getData().getString(Utils.BODY_FIELD_START_DATE);
                String endDate = message.getData().getString(Utils.BODY_FIELD_END_DATE);
                this.onAvailableRental(startDate, endDate);
            } else {
                this.showError(String.format("%s is unavailable during these dates.", rentalName), "Try again different dates.");
            }
            return true;
        }

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_page);

        final RentalPagePresenter presenter = new RentalPagePresenter(handler);

        JSONObject rentalInfo = new JSONObject();
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            try {
                rentalInfo = new JSONObject(Objects.requireNonNull(intent.getStringExtra(Utils.INTENT_KEY_RENTAL_INFO)));
            } catch (JSONException e) {
                Log.d("RentalPageActivity.onCreate()", "Could not get JSON of rental info from intent:\n" + e);
                throw new RuntimeException(e);
            }
        }

        // Fill page data with rental info
        try {
            TextView rentalNameText = findViewById(R.id.rental_name);
            rentalName = rentalInfo.getString(Utils.BODY_FIELD_RENTAL_NAME);
            rentalNameText.setText(rentalName);

            TextView rentalLocation = findViewById(R.id.rental_location);
            rentalLocation.setText(rentalInfo.getString(Utils.BODY_FIELD_RENTAL_LOCATION));

            RatingBar starsIndicator = findViewById(R.id.rental_stars);
            starsIndicator.setRating((float) rentalInfo.getDouble(Utils.BODY_FIELD_RENTAL_STARS));

            TextView capacity = findViewById(R.id.rental_capacity);
            capacity.setText(String.valueOf(rentalInfo.getInt(Utils.BODY_FIELD_RENTAL_CAPACITY)));

            TextView nightlyRate = findViewById(R.id.rental_nightly_rate);
            nightlyRate.setText(String.valueOf(rentalInfo.getDouble(Utils.BODY_FIELD_RENTAL_NIGHTLY_RATE)));
        } catch (JSONException e) {
            Log.d("RentalPageActivity.onCreate()", "Could not fill rental_page layout with rental's information:\n" + e);
            throw new RuntimeException(e);
        }

        // Home button
        ImageView headerLogo = findViewById(R.id.rentalpage_header_logo);
        headerLogo.setOnClickListener(view -> {
            Intent intent = new Intent(RentalPageActivity.this, HomePageActivity.class);
            startActivity(intent);
        });

        MaterialButton checkAvailabilityButton = findViewById(R.id.btn_check_availability);
        checkAvailabilityButton.setOnClickListener(view -> DatePickerDialog(presenter, 0)); // TODO
    }

    private void DatePickerDialog(RentalPagePresenter presenter, int rentalId) {
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

            presenter.onSelectDates(rentalId, startDate, endDate);
        });

        materialDatePicker.addOnNegativeButtonClickListener(view -> materialDatePicker.dismiss());

        // Show the date picker dialog
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void onAvailableRental(String startDate, String endDate) {
        // TODO: Show popup asking for booking
        this.showToast("Available");
    }

    // RentalPageView implementations
    @Override
    public void showError(String title, String msg) {
        new AlertDialog.Builder(this).setCancelable(true).setTitle(title).setMessage(msg).setPositiveButton(R.string.ok, null).create().show();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(RentalPageActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
