package com.example.dine_and_donate.Models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

@Parcel
@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public boolean isOrg;
    public String phoneNumber;
    public String bio;
    public Boolean prefersPhoneContact;
    public Map<String, String> savedEventsIDs = new HashMap<String, String>();
    public String imageUrl;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String imageUrl) {
        this.name = name;
        this.email = email;
        this.isOrg = false;
        this.imageUrl = imageUrl;
    }

    public User(String name, String email, String phoneNumber, String imageUrl) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isOrg = true;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsOrg() {
        return isOrg;
    }

    public void setOrg(boolean org) {
        this.isOrg = org;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Map<String, String> getSavedEventsIDs() {
        return savedEventsIDs;
    }

    public void addSavedEventID(Map<String, String> ids) {
        this.savedEventsIDs = ids;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}