package com.example.dine_and_donate.Models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.File;
import java.util.ArrayList;

@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public boolean isOrg;
    public String phoneNumber;
    public File profileImage;
    public String bio;
    public Boolean prefersPhoneContact;
    public ArrayList<Event> upcomingEvents;
    public ArrayList<Event> pastEvents;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        isOrg = false;
    }

    public User(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        isOrg = true;
    }


}