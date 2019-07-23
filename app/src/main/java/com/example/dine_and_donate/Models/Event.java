package com.example.dine_and_donate.Models;


import com.google.firebase.database.IgnoreExtraProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

@IgnoreExtraProperties
public class Event {
    public String orgId;
    public String yelpID;
    public String locationString;
    public long startTime;
    public long endTime;
    public String info;

    public Event() {}

    public Event(String orgId, String locationString, String yelpID, String info, long startTime, long endTime) {
        this.orgId = orgId;
        this.yelpID = yelpID;
        this.locationString = locationString;
        this.startTime = startTime;
        this.endTime = endTime;
        this.info = info;
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
}
