package com.example.dine_and_donate;

import com.example.dine_and_donate.Adapters.StaggeredRecyclerViewAdapter;
import com.example.dine_and_donate.Models.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class TabFragmentHelper {

    private ArrayList<Event> mEvents;
    private StaggeredRecyclerViewAdapter mStaggeredRecyclerViewAdapter;
    private boolean mForOld;
    private ArrayList<Long> mOtherMillies = new ArrayList<>();

    public TabFragmentHelper(ArrayList<Event> events, StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter, boolean forOld) {
        mEvents = events;
        mStaggeredRecyclerViewAdapter = staggeredRecyclerViewAdapter;
        mForOld = forOld;
    }

    //add images and descriptions to respective arrayLists
    public void initBitmapsEvents(String urls, String descriptions, long otherMillis) {
        Event newEvent = new Event();
        newEvent.locationString = descriptions;
        newEvent.imageUrl = urls;
        newEvent.endTime = otherMillis;
        mEvents.add(0, newEvent);
    }

    public void filterEvents() {
        long dateMillis = Calendar.getInstance().getTimeInMillis();
        ArrayList<Event> filteredEvents = new ArrayList<>();
        for (int index = 0; index < mEvents.size(); index++) {
            if ((!mForOld) && (dateMillis < mEvents.get(index).endTime)) {
                filteredEvents.add(mEvents.get(index));
            } else if ((mForOld) && (dateMillis >= mEvents.get(index).endTime)) {
                filteredEvents.add(mEvents.get(index));
            }
        }
        mEvents = filteredEvents;
        mStaggeredRecyclerViewAdapter.updateEvents(mEvents);
    }
}