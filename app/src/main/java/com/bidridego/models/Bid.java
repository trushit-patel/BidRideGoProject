package com.bidridego.models;

public class Bid {
    private String driverID;
    private double bidValue;

    public Bid(String driverID, double bidValue) {
        this.driverID = driverID;
        this.bidValue = bidValue;
    }

    public Bid(){}
    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public double getBidValue() {
        return bidValue;
    }

    public void setBidValue(double bidValue) {
        this.bidValue = bidValue;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        return this.driverID == ((Bid) obj).getDriverID();
    }
}
