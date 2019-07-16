package com.example.dine_and_donate.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

@ParseClassName("Event")
public class Event extends ParseObject {
    private Boolean isAproved;
    private String info;
    private Double percentOfMealPriceDonated;
    private ArrayList<Date> eventDates;
    private File voucher;
    private ArrayList<Consumer> consumers;
    private Organization organization;
    // could create restaurant class and include here
}
