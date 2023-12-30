package com.bidridego.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.bidridego.R;
import com.bidridego.models.BidDetails;
import com.bidridego.models.Trip;
import com.bidridego.models.User;
import com.bidridego.viewadapter.UserTripBidsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserTripBidsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserTripBidsAdapter adapter;
    public ArrayList<BidDetails> tripArrayList;
    private FirebaseDatabase firebaseDatabase;
    private HashMap<String, Double> bids;
    TextView bidsSource, bidsDestination, bidsDate, bidsTime;

    //    private DatabaseReference databaseReferenceToTrips;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_trip_bids);
        bidsSource = findViewById(R.id.bids_source);
        bidsDestination = findViewById(R.id.bids_destination);
        bidsDate = findViewById(R.id.bids_date);
        bidsTime = findViewById(R.id.bids_time);
        firebaseDatabase = FirebaseDatabase.getInstance();
        tripArrayList = new ArrayList<>();


        // Receive the trip object from the Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("trip")) {
            Trip trip = intent.getParcelableExtra("trip");

            // Now you have the 'trip' object, you can use it as needed
            if (trip != null) {
                // Example: Access trip details
                String source = trip.getFrom().getLocationName();
                String destination = trip.getTo().getLocationName();
                String[] dateTime = trip.getDateAndTime().split(" ");
                bidsDate.setText(dateTime[0]);
                bidsTime.setText(dateTime[1]);
                bidsSource.setText(source);
                bidsDestination.setText(destination);
                bids = trip.getBids();
                for (Map.Entry<String, Double> entry : bids.entrySet()) {
                    String driverId = entry.getKey();
                    Double bidValue = entry.getValue();
                    DatabaseReference usersRef = firebaseDatabase.getReference("users");
                    usersRef.child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User driver = dataSnapshot.getValue(User.class);
                                BidDetails bidDetails = new BidDetails(driverId, bidValue, driver.getId(), driver.getFirstName(), driver.getLastName(), driver.getContact());
                                tripArrayList.add(bidDetails);
                                adapter.notifyDataSetChanged();

                            } else {
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors
                        }
                    });
                }
            }

            recyclerView = findViewById(R.id.user_trip_bids);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Initialize Adapter
            adapter = new UserTripBidsAdapter(R.layout.user_trip_bids_list_item, tripArrayList, this);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new UserTripBidsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    BidDetails trip = tripArrayList.get(position);
                    Intent intent = new Intent(UserTripBidsActivity.this, UserBidDetailsActivity.class);
                    intent.putExtra("bidDetails",trip);
                    startActivity(intent);
                }
            });
        }
    }
}