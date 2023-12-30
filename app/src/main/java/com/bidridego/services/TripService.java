package com.bidridego.services;

import com.bidridego.models.BidRideLocation;
import com.bidridego.models.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TripService {
    private static TripService tripsService = null;
    private final FirebaseAuth auth;
    private final FirebaseDatabase firebaseDatabase;
    private final DatabaseReference databaseReferenceToTrips;
//    private final DatabaseReference databaseReferenceToUsers;
//    private UserService userService;

    private TripService() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        databaseReferenceToTrips = firebaseDatabase.getReference("trips");
//        databaseReferenceToUsers = firebaseDatabase.getReference("users");
//        userService = UserService.getInstance();
    }

    public static TripService getInstance() {
        if (tripsService == null) {
            tripsService = new TripService();
        }
        return tripsService;
    }

    public void saveOrUpdate(Trip trip, SaveOrUpdateCallback callback) {
        if (trip.getId() == null) {
            trip.setId(this.databaseReferenceToTrips.push().getKey());
        }
        trip.setPostedBy(auth.getCurrentUser().getUid());

        databaseReferenceToTrips.child(trip.getId()).setValue(trip)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save or update operation successful
                        callback.onSuccess();
                    } else {
                        // Save or update operation failed
                        callback.onFailure(task.getException().getMessage());
                    }
                });

        // Additional updates for 'to', 'from', 'minBid', 'bids' can be done similarly
        databaseReferenceToTrips.child(trip.getId()).child("to").setValue(trip.getTo());
        databaseReferenceToTrips.child(trip.getId()).child("from").setValue(trip.getFrom());
        databaseReferenceToTrips.child(trip.getId()).child("minBid").setValue(trip.getMinBid());
        databaseReferenceToTrips.child(trip.getId()).child("bids").setValue(trip.getBids());
    }
}
