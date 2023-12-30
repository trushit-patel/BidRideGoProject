package com.bidridego.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class BidDetails extends Bid implements Parcelable {
    private String id;
    private String firstName;
    private String lastName;
    private String contact;

    public BidDetails(String driverID, double bidValue, String id, String firstName, String lastName, String contact) {
        super(driverID, bidValue);
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public BidDetails(String id, String firstName, String lastName, String contact) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
    }

    protected BidDetails(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        contact = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(contact);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BidDetails> CREATOR = new Creator<BidDetails>() {
        @Override
        public BidDetails createFromParcel(Parcel in) {
            return new BidDetails(in);
        }

        @Override
        public BidDetails[] newArray(int size) {
            return new BidDetails[size];
        }
    };


}
