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
import com.bidridego.viewholder.PastTripViewHolder;
import com.bidridego.viewholder.UpcomingTripViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class UserPastTripAdapter extends RecyclerView.Adapter<PastTripViewHolder> {
    private int trip_row_layout;
    private ArrayList<Trip> tripList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public UserPastTripAdapter(int trip_row_layout_as_id, ArrayList<Trip> tripList, Context context) {
        this.trip_row_layout = trip_row_layout_as_id;
        this.tripList = tripList;
    }

    @Override
    public int getItemCount() {
        return tripList == null ? 0 : tripList.size();
    }

    @Override
    public PastTripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myTripView = LayoutInflater.from(parent.getContext()).inflate(trip_row_layout, parent, false);

        PastTripViewHolder myViewHolder = new PastTripViewHolder(myTripView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final PastTripViewHolder tripViewHolder, @SuppressLint("RecyclerView") final int listPosition) {
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

//            FirebaseDatabase.getInstance().getReference("users").child(currTrip.getPostedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
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
