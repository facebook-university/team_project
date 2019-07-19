package com.example.dine_and_donate.Models;

import java.util.ArrayList;

public class Consumer {

    private String email;
    private String name;
    private ArrayList<Event> upcomingEvents;
    private ArrayList<Event> pastEvents;
    private ArrayList<Organization> followingOrgs;


    public Consumer(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
