package com.example.dine_and_donate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.dine_and_donate.Adapters.StaggeredRecyclerViewAdapter;
import com.example.dine_and_donate.Models.Event;

import java.util.ArrayList;

public class OldVouchersFragment extends Fragment {

    private View v;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private ArrayList<Event> mEvents;

    //default constructor
    public OldVouchersFragment() {
        mEvents = new ArrayList<>();
    }

    //inflates layout of fragment
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab_fragment, container, false);
        mRecyclerView = v.findViewById(R.id.rv_vouchers);
        StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(getActivity(), mEvents);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(staggeredRecyclerViewAdapter);
        return v;
    }

    //initial creation of fragment
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBitmapsPastEvents();
    }

    //add images and descriptions to respective arrayLists
    private void initBitmapsPastEvents() {

        //test events
        for(int i = 0; i < 10; i++) {
            Event newEvent = new Event();
            newEvent.locationString = "test";
            newEvent.imageUrl = "https://i.redd.it/tpsnoz5bzo501.jpg";
            mEvents.add(newEvent);
        }
    }
}