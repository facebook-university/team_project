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
    public String imageUrl;

    public Event() {}

    public Event(String orgId, double locationLong, double locationLat, String locationString, long startTime, long endTime, String info) {
        this.orgId = orgId;
        this.locationLong = locationLong;
        this.locationLat = locationLat;
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

    public double getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(double locationLong) {
        this.locationLong = locationLong;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
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
