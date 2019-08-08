package com.example.dine_and_donate.Models;

public class Notification {

    private String mEventId; //event has to take place on that day
    private String mCreatedAt;
    private String mYelpId;
    private String mOrgId;

    public Notification() {

    }

    public Notification(String eventId, String yelpId, String createdAt, String orgPicUri) {
        this.mEventId = eventId;
        this.mCreatedAt = createdAt;
        this.mYelpId = yelpId;
    }

    public String getEventId() {
        return mEventId;
    }

    public void setEventId(String eventId) {
        this.mEventId = eventId;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.mCreatedAt = createdAt;
    }

    public String getYelpId() {
        return mYelpId;
    }

    public void setYelpId(String yelpId) {
        this.mYelpId = yelpId;
    }

    public String getOrgId() {
        return mOrgId;
    }

    public void setOrgId(String orgId) {
        this.mOrgId = orgId;
    }
}


