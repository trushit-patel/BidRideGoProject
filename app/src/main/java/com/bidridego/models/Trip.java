package com.bidridego.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Trip implements Parcelable {
    public Trip(String id, double cost, BidRideLocation from, BidRideLocation to, double distance, String postedBy, int passengers, String dateAndTime, boolean isCarPool, String rideType, double minBid, HashMap<String, Double> bids) {
        this.id = id;
        this.cost = cost;
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.postedBy = postedBy;
        this.passengers = passengers;
        this.dateAndTime = dateAndTime;
        this.isCarPool = isCarPool;
        this.rideType = rideType;
        this.minBid = minBid;
        this.bids = bids;
    }

    public Trip(){}

    private String id;
    private double cost;
    private BidRideLocation from;
    private BidRideLocation to;
    private double distance;
    private String postedBy;
    private int passengers;
    private String dateAndTime;
    private boolean isCarPool = false;
    private String rideType;
    private double minBid;
//    private ArrayList<Bid> bids = new ArrayList<>();
    private HashMap<String, Double> bids = new HashMap<>();

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public BidRideLocation getFrom() {
        return this.from;
    }

    public void setFrom(BidRideLocation from) {
        this.from = from;
    }

    public BidRideLocation getTo() {
        return this.to;
    }

    public void setTo(BidRideLocation to) {
        this.to = to;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getPostedBy() {
        return this.postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public int getPassengers() {
        return this.passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public boolean isCarPool() {
        return this.isCarPool;
    }

    public void setCarPool(boolean carPool) {
        this.isCarPool = carPool;
    }

    public String getRideType() {
        return this.rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }

    public double getMinBid() {
        return minBid;
    }

    public void setMinBid(double minBid) {
        this.minBid = minBid;
    }

    public HashMap<String, Double> getBids() {
        return bids;
    }

    public void setBids(HashMap<String, Double> bids) {
        this.bids = bids;
    }

    // Parcelable implementation
    protected Trip(Parcel in) {
        id = in.readString();
        cost = in.readDouble();
        from = in.readParcelable(BidRideLocation.class.getClassLoader());
        to = in.readParcelable(BidRideLocation.class.getClassLoader());
        distance = in.readDouble();
        postedBy = in.readString();
        passengers = in.readInt();
        dateAndTime = in.readString();
        isCarPool = in.readByte() != 0;
        rideType = in.readString();
        minBid = in.readDouble();
        bids = new HashMap<>();
        in.readMap(bids, Double.class.getClassLoader());
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(cost);
        dest.writeParcelable(from, flags);
        dest.writeParcelable(to, flags);
        dest.writeDouble(distance);
        dest.writeString(postedBy);
        dest.writeInt(passengers);
        dest.writeString(dateAndTime);
        dest.writeByte((byte) (isCarPool ? 1 : 0));
        dest.writeString(rideType);
        dest.writeDouble(minBid);
        dest.writeMap(bids);
    }
}
