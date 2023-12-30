package com.bidridego.user;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bidridego.R;
import com.bidridego.driver.BidingDialog;
import com.bidridego.models.Trip;
import com.bidridego.utils.DateTimeUtils;
import com.bidridego.viewadapter.UserUpcomingTripAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * A fragment representing a list of Items.
 */
public class FragmentUpcomingTrips extends Fragment {

    private RecyclerView recyclerView;
    private UserUpcomingTripAdapter adapter;
    public ArrayList<Trip> tripArrayList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceToTrips;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_fragment_upcoming_trips_list, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceToTrips = firebaseDatabase.getReference("trips");
        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.user_upcoming_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tripArrayList = new ArrayList <>();
        // Initialize Adapter
        adapter = new UserUpcomingTripAdapter(R.layout.user_fragment_upcoming_trips, tripArrayList, getContext());
        recyclerView.setAdapter(adapter);

        String filterDate;
        try {
            filterDate = DateTimeUtils.getTimeStampFromDate(new Date());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String userId = FirebaseAuth.getInstance().getUid();
        databaseReferenceToTrips.orderByChild("dateAndTime").startAt(filterDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    if (trip != null && trip.getPostedBy().equals(userId)) {
                        tripArrayList.add(trip);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.setOnItemClickListener(new UserUpcomingTripAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Trip trip = tripArrayList.get(position);
//                Bundle bundle = new Bundle();
                Intent intent = new Intent(getActivity(), UserTripBidsActivity.class);
                intent.putExtra("trip",trip);
                startActivity(intent);
            }
        });
        return rootView;
    }

}