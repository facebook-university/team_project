package com.example.dine_and_donate.Models;


import com.google.firebase.database.IgnoreExtraProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

@IgnoreExtraProperties
public class Event {
    public String orgId;
    public double locationLong;
    public double locationLat;
    public String locationString;
    public long startTime;
    public long endTime;
    public String info;

    public Event() {

    }

    public Event(String orgId, double locationLong, double locationLat, String locationString, long startTime, long endTime, String info) {
        this.orgId = orgId;
        this.locationLong = locationLong;
        this.locationLat = locationLat;
        this.locationString = locationString;
        this.startTime = startTime;
        this.endTime = endTime;
        this.info = info;
    }
}
