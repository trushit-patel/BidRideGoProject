package com.bidridego.viewadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bidridego.models.BidRideLocation;
import com.bidridego.models.Trip;
import com.bidridego.viewholder.UpcomingTripViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class UserUpcomingTripAdapter extends RecyclerView.Adapter<UpcomingTripViewHolder> {
    private int trip_row_layout;
    private ArrayList<Trip> tripList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public UserUpcomingTripAdapter(int trip_row_layout_as_id, ArrayList<Trip> tripList, Context context) {
        this.trip_row_layout = trip_row_layout_as_id;
        this.tripList = tripList;
    }

    @Override
    public int getItemCount() {
        return tripList == null ? 0 : tripList.size();
    }

    @Override
    public UpcomingTripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myTripView = LayoutInflater.from(parent.getContext()).inflate(trip_row_layout, parent, false);

        UpcomingTripViewHolder myViewHolder = new UpcomingTripViewHolder(myTripView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final UpcomingTripViewHolder tripViewHolder, @SuppressLint("RecyclerView") final int listPosition) {
        TextView source = tripViewHolder.source;
        TextView destination = tripViewHolder.destination;
        TextView minBid = tripViewHolder.minBid;
        TextView date = tripViewHolder.date;
        TextView time = tripViewHolder.time;

        Trip currTrip = this.tripList.get(listPosition);


        if(currTrip != null){
//            date.setText(currTrip.getDate());
//            time.setText(currTrip.getTime());

            String[] dateTime = currTrip.getDateAndTime().split(" ");
            date.setText(dateTime[0]);
            time.setText(dateTime[1]);

            FirebaseDatabase.getInstance().getReference("users").child(currTrip.getPostedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            minBid.setText("" + currTrip.getMinBid());

            BidRideLocation to = currTrip.getTo();
            BidRideLocation from = currTrip.getFrom();

            if(to != null) destination.setText(to.getLocationName());
            if(from != null) source.setText(from.getLocationName());
        }
        tripViewHolder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(listPosition);
            }
        });
    }
}
