package com.example.dine_and_donate.Models;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class Event {
    private Boolean isAproved;
    private String info;
    private Double percentOfMealPriceDonated;
    private ArrayList<Date> eventDates;
    private File voucher;
    private ArrayList<Consumer> consumers;
    private Organization organization;
    // could create restaurant class and include here
}