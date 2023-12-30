package com.bidridego.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bidridego.R;

public class PastTripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView source,  destination, minBid, budget, date, time;
    public Switch isCarPool;

    public PastTripViewHolder(View tripView) {
        super(tripView);
        source = tripView.findViewById(R.id.user_past_source);
        destination = tripView.findViewById(R.id.user_past_destination);
        minBid = tripView.findViewById(R.id.user_past_min_bid);
        date = tripView.findViewById(R.id.user_past_date);
        time = tripView.findViewById(R.id.user_past_time);
        tripView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d("onclick", "onClick "
                + getLayoutPosition() + " " + destination.getText());
    }
}