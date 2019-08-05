package com.example.dine_and_donate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.dine_and_donate.Adapters.StaggeredRecyclerViewAdapter;
import com.example.dine_and_donate.Models.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class OldVouchersFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;

    private Context mContext;
    private ArrayList<Event> mEvents;

    //default constructor
    public OldVouchersFragment() {
        mEvents = new ArrayList<>();
    }

    //inflates layout of fragment
    private Map<String, String> pastEvents;
    private User mCurrUser;
    private DatabaseReference mRef;
    private DatabaseReference mRefForEvent;
    private StaggeredRecyclerViewAdapter mStaggeredRecyclerViewAdapter;

    //inflates layout of fragment
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_fragment, container, false);
        mRecyclerView = mView.findViewById(R.id.rv_vouchers);
        mStaggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(getActivity(), mEvents);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(mStaggeredRecyclerViewAdapter);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRef = FirebaseDatabase.getInstance().getReference();
        mRefForEvent = mRef.child("events");

        HomeActivity homeActivity = (HomeActivity) getActivity();
        mCurrUser = homeActivity.currentUser;
        pastEvents = mCurrUser.getSavedEventsIDs();
        final long dateMillis = Calendar.getInstance().getTimeInMillis();
        mRefForEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //look through all restaurants
                for (DataSnapshot dsRestaurant : dataSnapshot.getChildren()) {
                    //iterate through all events at that restaurant
                    for (DataSnapshot dsEvent : dsRestaurant.getChildren()) {
                        //that event is saved, should be added to arrayList
                        if (pastEvents.containsKey(dsEvent.getKey())) {
                            //if event end date is older than today's date, it is a past event
                            if (Long.toString(dateMillis).compareTo(dsEvent.child("endTime").toString()) > 0) {
                                initBitmapsPastEvents(dsEvent.child("imageUrl").getValue().toString(), dsEvent.child("locationString").getValue().toString());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRecyclerView = view.findViewById(R.id.rv_vouchers);
        StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(getActivity(), mEvents);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(staggeredRecyclerViewAdapter);
    }

    //add images and descriptions to respective arrayLists
    private void initBitmapsPastEvents(String mUrls, String mDescriptions) {
        //test events
        Event newEvent = new Event();
        newEvent.locationString = mDescriptions;
        newEvent.imageUrl = mUrls;
        mEvents.add(newEvent);
        mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
    }
}