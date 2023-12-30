package com.bidridego.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bidridego.R;

public class UpcomingTripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView source,  destination, minBid, budget, date, time;
    public Switch isCarPool;

    public UpcomingTripViewHolder(View tripView) {
        super(tripView);
        source = tripView.findViewById(R.id.user_upcoming_source);
        destination = tripView.findViewById(R.id.user_upcoming_destination);
        minBid = tripView.findViewById(R.id.user_upcoming_min_bid);
        date = tripView.findViewById(R.id.user_upcoming_date);
        time = tripView.findViewById(R.id.user_upcoming_time);
        tripView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d("onclick", "onClick "
                + getLayoutPosition() + " " + destination.getText());
    }
}