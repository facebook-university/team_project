package com.example.dine_and_donate.Models;

public class Notifications {

    private String eventId; //event has to take place on that day
    private String createdAt;
    private String yelpId;

    public Notifications() {

    }

    public Notifications(String eventId, String yelpId) {
        this.eventId = eventId;
//        this.createdAt = createdAt;
        this.yelpId = yelpId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getYelpId() {
        return yelpId;
    }

    public void setYelpId(String yelpId) {
        this.yelpId = yelpId;
    }
}
