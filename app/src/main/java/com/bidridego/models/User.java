package com.bidridego.models;

import java.util.HashSet;

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String contact;
    //    private String address = "";
    private String password;
    private String role;
    private HashSet<String> trips = null;

    public User(String firstName, String lastName, String contact, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
        this.role = role;
//        this.address = address;
    }
    public User() {}

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    //    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HashSet<String> getTrips() {
        return trips;
    }

    public void setTrips(HashSet<String> trips) {
        this.trips = trips;
    }
}
