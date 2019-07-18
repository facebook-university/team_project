package com.example.dine_and_donate.Models;

import java.io.File;
import java.util.ArrayList;

public class Organization {

    private String email;
    private String name;
    private File profileImage;
    private String bio;
    private String phoneNumber;
    private Boolean prefersPhoneContact;
    private ArrayList<Event> upcomingEvents;
    private ArrayList<Event> pastEvents;

    public Organization(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
