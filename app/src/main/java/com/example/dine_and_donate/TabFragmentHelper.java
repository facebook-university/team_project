package com.example.dine_and_donate;

import android.util.Log;

import com.example.dine_and_donate.Adapters.StaggeredRecyclerViewAdapter;
import com.example.dine_and_donate.Models.Event;

import java.util.ArrayList;

public class TabFragmentHelper {

    private ArrayList<Event> mEvents;
    private StaggeredRecyclerViewAdapter mStaggeredRecyclerViewAdapter;
    private boolean mForOld;

    public TabFragmentHelper(ArrayList<Event> events, StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter, boolean forOld) {
        mEvents = events;
        mStaggeredRecyclerViewAdapter = staggeredRecyclerViewAdapter;
        mForOld = forOld;
    }

    public void initBitmapsEvents(String urls, String descriptions) {
        Event newEvent = new Event();
        newEvent.locationString = descriptions;
        newEvent.imageUrl = urls;
        //for upcoming vouchers
        mEvents.add(0, newEvent);
        mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
    }
}