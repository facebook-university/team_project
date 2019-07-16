package com.example.dine_and_donate.Models;

import com.parse.Parse;
import com.parse.ParseUser;

import java.util.ArrayList;

public class Consumer extends ParseUser {

    private String email;
    private String password;
    private ArrayList<Event> upcomingEvents;
    private ArrayList<Event> pastEvents;
    private ArrayList<Organization> followingOrgs;


}
