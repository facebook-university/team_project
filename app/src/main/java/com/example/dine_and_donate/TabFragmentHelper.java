package com.example.dine_and_donate;

import com.example.dine_and_donate.Adapters.StaggeredRecyclerViewAdapter;
import com.example.dine_and_donate.Models.Event;

import java.util.ArrayList;

public class TabFragmentHelper {

    private ArrayList<Event> mEvents;
    private StaggeredRecyclerViewAdapter mStaggeredRecyclerViewAdapter;

    public TabFragmentHelper(ArrayList<Event> events, StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter) {
        mEvents = events;
        mStaggeredRecyclerViewAdapter = staggeredRecyclerViewAdapter;
    }

    //add images and descriptions to respective arrayLists
    public void initBitmapsEvents(String urls, String descriptions) {
        Event newEvent = new Event();
        newEvent.locationString = descriptions;
        newEvent.imageUrl = urls;
        mEvents.add(newEvent);
        mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
    }
}
