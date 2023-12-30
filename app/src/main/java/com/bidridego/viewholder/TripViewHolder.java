package com.bidridego.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bidridego.R;

public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView cost, destination, source, minBid,  date, time, tripWhos;
    public TripViewHolder(View tripView) {
        super(tripView);
        date = tripView.findViewById(R.id.trip_date);
        time = tripView.findViewById(R.id.trip_time);
        cost = tripView.findViewById(R.id.trip_cost);
        destination = tripView.findViewById(R.id.trip_destination);
        source = tripView.findViewById(R.id.trip_source);
        minBid = tripView.findViewById(R.id.trip_min_bid);
        tripWhos = tripView.findViewById(R.id.trip_whos);
        tripView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d("onclick", "onClick "
                + getLayoutPosition() + " " + destination.getText());
    }
}