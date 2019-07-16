package com.example.dine_and_donate.Models;

import com.parse.ParseUser;

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

}
