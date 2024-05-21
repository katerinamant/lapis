package com.example.lapis.RentalPage;

import static java.time.temporal.ChronoUnit.DAYS;

import android.content.Intent;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RentalPageActivity extends AppCompatActivity {
    RelativeLayout relativeLayout;
    RentalPagePresenter presenter;
    String rentalName, rentalLocation;
    double nightlyRate;
    final PopupWindow[] newBookingPopup = {null};
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

        // Message was from NewBookingThread
        String status = message.getData().getString(Utils.BODY_FIELD_STATUS);
        if (newBookingPopup[0] != null) {
            newBookingPopup[0].dismiss();
        }
        assert status != null;
        if (status.equals("OK")) {
            this.showError("Booking was successful!", "Looking forward to hosting you!");
        } else {
            this.showError("Booking was unsuccessful!", "Please try again different days");
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_page);

        relativeLayout = findViewById(R.id.relative_rental_page);

        presenter = new RentalPagePresenter(handler, 0); // TODO

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

            TextView rentalLocationText = findViewById(R.id.rental_location);
            rentalLocation = rentalInfo.getString(Utils.BODY_FIELD_RENTAL_LOCATION);
            rentalLocationText.setText(rentalLocation);

            RatingBar starsIndicator = findViewById(R.id.rental_stars);
            starsIndicator.setRating((float) rentalInfo.getDouble(Utils.BODY_FIELD_RENTAL_STARS));

            TextView capacity = findViewById(R.id.rental_capacity);
            capacity.setText(String.valueOf(rentalInfo.getInt(Utils.BODY_FIELD_RENTAL_CAPACITY)));

            TextView nightlyRateText = findViewById(R.id.rental_nightly_rate);
            nightlyRate = rentalInfo.getDouble(Utils.BODY_FIELD_RENTAL_NIGHTLY_RATE);
            nightlyRateText.setText(String.valueOf(nightlyRate));
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
        checkAvailabilityButton.setOnClickListener(view -> datePickerDialog());
    }

    private void datePickerDialog() {
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

            presenter.onSelectDates(startDate, endDate);
        });

        materialDatePicker.addOnNegativeButtonClickListener(view -> materialDatePicker.dismiss());

        // Show the date picker dialog
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void onAvailableRental(String startDateString, String endDateString) {
        // Inflate popup layout
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View pop_up = layoutInflater.inflate(R.layout.popup_new_booking, null);

        // Create and show confirm rating popup
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        // The array is used because
        // the variable needs to be final
        // for the onClickListener
        newBookingPopup[0] = new PopupWindow(pop_up, width, height, true);
        newBookingPopup[0].showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);

        // Fill popup information
        TextView rentalNameText = pop_up.findViewById(R.id.popup_booking_rental_name);
        rentalNameText.setText(rentalName);
        TextView rentalLocationText = pop_up.findViewById(R.id.popup_booking_rental_location);
        rentalLocationText.setText(rentalLocation);
        TextView datesText = pop_up.findViewById(R.id.popup_booking_dates);
        datesText.setText(String.format("[%s - %s]", startDateString, endDateString));
        // Calculate total cost
        LocalDate startDate = LocalDate.parse(startDateString, Utils.dateFormatter);
        LocalDate endDate = LocalDate.parse(endDateString, Utils.dateFormatter);
        int daysBetween = (int) DAYS.between(startDate.atStartOfDay(), endDate.atStartOfDay());
        double totalCost = daysBetween * nightlyRate;
        TextView totalCostText = pop_up.findViewById(R.id.popup_booking_total_cost);
        totalCostText.setText(String.valueOf(totalCost));

        Button cancelButton = pop_up.findViewById(R.id.popup_booking_cancel_btn);
        cancelButton.setOnClickListener(v -> {
            newBookingPopup[0].dismiss();
            newBookingPopup[0] = null;
        });

        Button confirmButton = pop_up.findViewById(R.id.popup_booking_confirm_btn);
        confirmButton.setOnClickListener(v -> {
            presenter.onConfirmBooking("guest@example.com", startDateString, endDateString); // TODO
        });
    }

    public void showError(String title, String msg) {
        new AlertDialog.Builder(this).setCancelable(true).setTitle(title).setMessage(msg).setPositiveButton(R.string.ok, null).create().show();
    }
}
