package com.example.lapis.RentalRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lapis.R;
import com.example.lapis.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RentalRecyclerViewAdapter extends RecyclerView.Adapter<RentalRecyclerViewAdapter.ViewHolder> {
    private final List<JSONObject> rentals;
    private final RecyclerViewActivity activity;

    public RentalRecyclerViewAdapter(List<JSONObject> rentals, RecyclerViewActivity listener) {
        this.rentals = rentals;
        this.activity = listener;
    }

    @NonNull
    @Override
    public RentalRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RentalRecyclerViewAdapter.ViewHolder holder, int position) {
        final JSONObject currentRental = rentals.get(position);
        holder.setRentalInfo(currentRental);
        String rentalName, rentalLocation, imgUrl;
        double rentalStars, rentalNightlyRate;
        int rentalNumOfRatings;

        try {
            rentalName = currentRental.getString(Utils.BODY_FIELD_RENTAL_NAME);
            rentalLocation = currentRental.getString(Utils.BODY_FIELD_RENTAL_LOCATION);
            rentalStars = currentRental.getDouble(Utils.BODY_FIELD_RENTAL_STARS);
            rentalNumOfRatings = currentRental.getInt(Utils.BODY_FIELD_RENTAL_RATINGS_NUM);
            rentalNightlyRate = currentRental.getDouble(Utils.BODY_FIELD_RENTAL_NIGHTLY_RATE);
            imgUrl = currentRental.getString(Utils.BODY_FIELD_RENTAL_IMAGE_URL);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        holder.name.setText(rentalName);
        holder.location.setText(rentalLocation);
        holder.stars.setText(String.valueOf(rentalStars));
        holder.numOfRatings.setText(String.valueOf(rentalNumOfRatings));
        holder.nightlyRate.setText(String.valueOf(rentalNightlyRate));
        Glide.with((Context) activity).load(imgUrl).into(holder.rentalImage);
    }


    @Override
    public int getItemCount() {
        return rentals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView name, location, stars, numOfRatings, nightlyRate;
        public final ImageView rentalImage;

        private JSONObject rentalInfo;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.rental_item_name);
            this.location = view.findViewById(R.id.rental_item_location);
            this.stars = view.findViewById(R.id.rental_item_stars);
            this.numOfRatings = view.findViewById(R.id.rental_item_no_ratings);
            this.nightlyRate = view.findViewById(R.id.rental_item_rate);
            this.rentalImage = view.findViewById(R.id.rental_item_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            activity.goToRentalPage(this.rentalInfo);
        }

        public void setRentalInfo(JSONObject rentalInfo) {
            this.rentalInfo = rentalInfo;
        }

    }
}
