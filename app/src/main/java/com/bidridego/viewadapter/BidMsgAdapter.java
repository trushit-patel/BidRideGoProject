package com.bidridego.viewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bidridego.R;
import com.bidridego.models.BidMsg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BidMsgAdapter extends ArrayAdapter<BidMsg> {

    public BidMsgAdapter(Context context, ArrayList<BidMsg> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the layout for each list item if needed
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bid_msg, parent, false);
        }

        // Get the BidMsg object for this position
        BidMsg bidMsg = getItem(position);

        // Bind data to TextViews in the list item layout (bid_msg_item.xml)
        TextView timestampTextView = convertView.findViewById(R.id.timestampTextView);
        TextView numericValueTextView = convertView.findViewById(R.id.numericValueTextView);

        if (bidMsg != null) {
            String timestamp = bidMsg.getTimestamp();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = null;
            try {
                date = inputFormat.parse(timestamp);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }


            // Format the Date object to get just the hours and minutes (HH:mm)
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String time = outputFormat.format(date);
            timestampTextView.setText(time);
            numericValueTextView.setText(String.valueOf(bidMsg.getNumericValue()));
        }

        return convertView;
    }
}
