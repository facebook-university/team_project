package com.example.dine_and_donate;

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
import com.example.dine_and_donate.Adapters.StaggeredRecyclerViewAdapter;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class UpcomingVouchersFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private Map<String, String> mSavedEventsIDs;
    private User mCurrUser;
    private DatabaseReference mRef;
    private DatabaseReference mRefForEvent;
    private ArrayList<Event> mEvents = new ArrayList<>();

    //create view based on data in array lists, inflates the layout of the fragment
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRef = FirebaseDatabase.getInstance().getReference();
        mRefForEvent = mRef.child("events");

        HomeActivity homeActivity = (HomeActivity) getActivity();
        mCurrUser = homeActivity.currentUser;
        mSavedEventsIDs = mCurrUser.getSavedEventsIDs();
        mRefForEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //look through all restaurants
                for(DataSnapshot dsRestaurant : dataSnapshot.getChildren()) {
                    //iterate through all events at that restaurant
                    for(DataSnapshot dsEvent : dsRestaurant.getChildren()) {
                        //that event is saved, should be added to arrayList
                        if(mSavedEventsIDs.containsKey(dsEvent.getKey())) {
                            initBitmapsUpcomingEvents(dsEvent.getValue(Event.class));
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
    private void initBitmapsUpcomingEvents(Event event) {
        mEvents.add(event);

    }
}