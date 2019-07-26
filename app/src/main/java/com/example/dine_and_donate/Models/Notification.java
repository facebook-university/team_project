package com.example.dine_and_donate.Models;

public class Notification {

    private Event mEvent; //event has to take place on that day

    public Notification(Event nearByEvent) {
        mEvent = nearByEvent;
    }
}
