package com.example.lapis.UserPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapis.R;
import com.example.lapis.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RatingsRecyclerViewAdapter extends RecyclerView.Adapter<RatingsRecyclerViewAdapter.ViewHolder> {
    private final List<JSONObject> ratings;
    private final UserPageActivity activity;

    public RatingsRecyclerViewAdapter(List<JSONObject> ratings, UserPageActivity listener) {
        this.ratings = ratings;
        this.activity = listener;
    }

    @NonNull
    @Override
    public RatingsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RatingsRecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RatingsRecyclerViewAdapter.ViewHolder holder, int position) {
        final JSONObject currentBooking = ratings.get(position);
        holder.setBookingInfo(currentBooking);
        String rentalName, rentalLocation, bookingDates;
        try {
            rentalName = currentBooking.getString(Utils.BODY_FIELD_RENTAL_NAME);
            rentalLocation = currentBooking.getString(Utils.BODY_FIELD_RENTAL_LOCATION);
            bookingDates = currentBooking.getString(Utils.BODY_FIELD_BOOKING_DATES_STRING);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        holder.rentalName.setText(rentalName);
        holder.rentalLocation.setText(rentalLocation);
        holder.bookingDates.setText(bookingDates);
    }


    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView rentalName, rentalLocation, bookingDates;
        private JSONObject bookingInfo;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.rentalName = view.findViewById(R.id.rating_item_rental_name);
            this.rentalLocation = view.findViewById(R.id.rating_item_rental_location);
            this.bookingDates = view.findViewById(R.id.rating_item_dates);
        }

        public void setBookingInfo(JSONObject bookingInfo) {
            this.bookingInfo = bookingInfo;
        }
    }
}
