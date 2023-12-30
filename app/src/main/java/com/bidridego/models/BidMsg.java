package com.bidridego.models;

public class BidMsg {
    String trip_id;
    String user_id;
    private String timestamp;
    private float numericValue;

    public BidMsg(String timestamp, float numericValue) {
        this.timestamp = timestamp;
        this.numericValue = numericValue;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public float getNumericValue() {
        return numericValue;
    }
}
