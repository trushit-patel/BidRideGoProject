package com.bidridego.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bidridego.R;
import com.bidridego.models.BidDetails;
import com.bidridego.models.Trip;
import com.bidridego.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class UserBidDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bid_details);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("bidDetails")) {
            BidDetails bidDetails = intent.getParcelableExtra("bidDetails");
            if (bidDetails != null) {

            }
        }
    }
}