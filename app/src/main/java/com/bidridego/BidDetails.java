package com.bidridego;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bidridego.models.BidMsg;
import com.bidridego.models.BidMsgDBHelper;
import com.bidridego.viewadapter.BidMsgAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BidDetails extends AppCompatActivity {
    String driver="Robin";
    float rating=5, initialBid = 35.2F;
    TextView driverNameView, bidView;
    private ArrayList<BidMsg> messageList;
    private EditText newBidTextView;
    private ListView listView;
    private BidMsgAdapter bidMsgAdapter;
    private FirebaseAuth mAuth;
    Button acceptBidBtn;
    private BidMsgDBHelper dbHelper;
    String userId, customerId, tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_details);

        setTitle("Bid Details");
        messageList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        Bundle receivedBundle = getIntent().getExtras();

        customerId = receivedBundle.getString("customerId");
        tripId = receivedBundle.getString("tripId");

        dbHelper = new BidMsgDBHelper(this);

        driverNameView = findViewById(R.id.driver);
        driverNameView.setText(driver);

        bidView = findViewById(R.id.bid);
        bidView.setText(bidView.getText()+" "+ String.valueOf(initialBid));


        listView = findViewById(R.id.listView);
        bidMsgAdapter = new BidMsgAdapter(this, messageList);
        listView.setAdapter(bidMsgAdapter);
        fetchAllBidMessages();

        newBidTextView = findViewById(R.id.newBid);
        acceptBidBtn = findViewById(R.id.accept);

        acceptBidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the bid value from EditText
                String newBidValueString = newBidTextView.getText().toString();

                // Check if the new bid value is not empty
                if (!newBidValueString.isEmpty()) {
                    float newBidValue = Float.parseFloat(newBidValueString);

                    // Create a new BidMsg object with current timestamp and new bid value
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    BidMsg newBid = new BidMsg(timestamp, newBidValue);

                    // Add the new bid to the list
                    messageList.add(newBid);
                    insertBidMessage(newBid);

                    // Notify the adapter that the dataset has changed
                    bidMsgAdapter.notifyDataSetChanged();

                    // Clear the EditText after adding the bid
                    newBidTextView.setText("");
                }
            }
        });
    }
    private void insertBidMessage(BidMsg bidMsg) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BidMsgDBHelper.COLUMN_TIMESTAMP, bidMsg.getTimestamp());
        values.put(BidMsgDBHelper.COLUMN_NUMERIC_VALUE, bidMsg.getNumericValue());
        db.insert(BidMsgDBHelper.TABLE_NAME, null, values);
        db.close();
    }
    private void fetchAllBidMessages(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(BidMsgDBHelper.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor != null) {
            while (!cursor.isAfterLast()){
                String timestamp = "";
                float numericValue = 0;
                int index = cursor.getColumnIndex(BidMsgDBHelper.COLUMN_TIMESTAMP);
                if(index >= 0) {
                    timestamp = cursor.getString(index);
                }
                index = cursor.getColumnIndex(BidMsgDBHelper.COLUMN_NUMERIC_VALUE);
                if(index >= 0) {
                    numericValue = cursor.getFloat(index);
                }

                // Create BidMsg object and add to messageList
                BidMsg bidMsg = new BidMsg(timestamp, numericValue);
                messageList.add(bidMsg);
                cursor.moveToNext();
            }

            // Notify the adapter that the data set has changed
            bidMsgAdapter.notifyDataSetChanged();

            // Close the cursor after use
            cursor.close();
        }

        // Close the database connection
        db.close();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}