package com.example.lapis.SearchPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapis.HomePage.HomePageActivity;
import com.example.lapis.R;
import com.example.lapis.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SearchResultsRecyclerViewAdapter extends RecyclerView.Adapter<SearchResultsRecyclerViewAdapter.ViewHolder> {
    private final List<JSONObject> rentals;
    private final SearchPageActivity activity;

    public SearchResultsRecyclerViewAdapter(List<JSONObject> rentals, SearchPageActivity listener) {
        this.rentals = rentals;
        this.activity = listener;
    }

    @NonNull
    @Override
    public SearchResultsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchResultsRecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsRecyclerViewAdapter.ViewHolder holder, int position) {
        final JSONObject currentRental = rentals.get(position);
        holder.setRentalInfo(currentRental);
        String rentalName, rentalLocation;
        double rentalNightlyRate;
        try {
            rentalName = currentRental.getString(Utils.BODY_FIELD_RENTAL_NAME);
            rentalLocation = currentRental.getString(Utils.BODY_FIELD_RENTAL_LOCATION);
            rentalNightlyRate = currentRental.getDouble(Utils.BODY_FIELD_RENTAL_NIGHTLY_RATE);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        holder.name.setText(rentalName);
        holder.location.setText(rentalLocation);
        holder.nightlyRate.setText(String.valueOf(rentalNightlyRate));
    }


    @Override
    public int getItemCount() {
        return rentals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView name, location, nightlyRate;
        private JSONObject rentalInfo;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.rental_item_name);
            this.location = view.findViewById(R.id.rental_item_location);
            this.nightlyRate = view.findViewById(R.id.rental_item_rate);
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

