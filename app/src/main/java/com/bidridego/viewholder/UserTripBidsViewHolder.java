package com.bidridego.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bidridego.R;

public class UserTripBidsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView driverName, bidValue;
    public UserTripBidsViewHolder(View tripView) {
        super(tripView);
        driverName = tripView.findViewById(R.id.bids_driver_name);
        bidValue = tripView.findViewById(R.id.bids_cost);
        tripView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}