package com.bidridego.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BidRideLocation implements Parcelable {
    public BidRideLocation(double lat, double lng, String locationName) {
        this.lat = lat;
        this.lng = lng;
        this.locationName = locationName;
    }

    public BidRideLocation(){}

    private double lat;
    private double lng;
    private  String locationName;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    protected BidRideLocation(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
        locationName = in.readString();
    }

    public static final Creator<BidRideLocation> CREATOR = new Creator<BidRideLocation>() {
        @Override
        public BidRideLocation createFromParcel(Parcel in) {
            return new BidRideLocation(in);
        }

        @Override
        public BidRideLocation[] newArray(int size) {
            return new BidRideLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(locationName);
    }
}
