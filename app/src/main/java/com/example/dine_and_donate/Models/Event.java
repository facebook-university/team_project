package com.example.dine_and_donate.Models;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

@Parcel
@IgnoreExtraProperties
public class Event {
    public String orgId;
    public String yelpID;
    public String locationString;
    public long startTime;
    public long endTime;
    public String info;
    public String imageUrl;
    public String eventId;

    public Event() {}

    public Event(String orgId, String yelpID, String locationString, long startTime, long endTime, String info, String imageUrl, String eventId) {
        this.orgId = orgId;
        this.yelpID = yelpID;
        this.locationString = locationString;
        this.startTime = startTime;
        this.endTime = endTime;
        this.info = info;
        this.imageUrl = imageUrl;
        this.eventId = eventId;
    }

    public static ArrayList<Event> eventsHappeningAtRestaurant(DataSnapshot ds) {
        ArrayList<Event> events = new ArrayList<>();
        for(DataSnapshot snapshot : ds.getChildren()) {
            events.add(snapshot.getValue(Event.class));
        }
        return events;
    }


    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getYelpID() {
        return this.yelpID;
    }

    public void setYelpID(String yelpID) {
        this.yelpID = yelpID;
    }

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
