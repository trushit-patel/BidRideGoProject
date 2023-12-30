package com.bidridego.viewadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bidridego.models.BidDetails;
import com.bidridego.models.BidRideLocation;
import com.bidridego.models.Trip;
import com.bidridego.utils.DateTimeUtils;
import com.bidridego.viewholder.TripViewHolder;
import com.bidridego.viewholder.UserTripBidsViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UserTripBidsAdapter extends RecyclerView.Adapter<UserTripBidsViewHolder> {
    private int trip_row_layout;
    private ArrayList<BidDetails> tripList;
    private UserTripBidsAdapter.OnItemClickListener onItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public UserTripBidsAdapter(int trip_row_layout_as_id, ArrayList<BidDetails> tripList, Context context) {
        this.trip_row_layout = trip_row_layout_as_id;
        this.tripList = tripList;
    }

    @Override
    public int getItemCount() {
        return tripList == null ? 0 : tripList.size();
    }

    @Override
    public UserTripBidsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myTripView = LayoutInflater.from(parent.getContext()).inflate(trip_row_layout, parent, false);

        UserTripBidsViewHolder myViewHolder = new UserTripBidsViewHolder(myTripView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final UserTripBidsViewHolder tripViewHolder, @SuppressLint("RecyclerView") final int listPosition) {
        TextView bidValue = tripViewHolder.bidValue;
        TextView driverName = tripViewHolder.driverName;

        BidDetails currTrip = this.tripList.get(listPosition);
        if(currTrip != null) {
            bidValue.setText(String.valueOf(currTrip.getBidValue()));
            driverName.setText(currTrip.getFirstName() + " " + currTrip.getLastName());
        }
        tripViewHolder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(listPosition);
            }
        });
    }
}
