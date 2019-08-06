package com.example.dine_and_donate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private View mView;
    private TextView mEmptyView;
    private int mSize;
    private StaggeredRecyclerViewAdapter mStaggeredRecyclerViewAdapter;
    private ArrayList<Event> mEvents = new ArrayList<>();

    //create view based on data in array lists, inflates the layout of the fragment
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        mCurrUser = homeActivity.currentUser;

        mView = inflater.inflate(R.layout.tab_fragment, container, false);
        mRecyclerView = mView.findViewById(R.id.rv_vouchers);
        mEmptyView = mView.findViewById(R.id.empty_view);
        mStaggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(getActivity(), mEvents);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mStaggeredRecyclerViewAdapter);
        return mView;
    }

    //case 1: opens on profile when user first opens app (adapter = 0)
    //case 2: switch from profile to map then back to map (adapter stays the same)
    //case 3: save a new event on map and then go back to profile
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mView, savedInstanceState);
//        int prevSize = mSavedEventsIDs.size();
        Log.d("prevSize", mSize + "");
//        Log.d("currSize", mSavedEventsIDs.size() + "");

        //for the first
        if(mStaggeredRecyclerViewAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.GONE);
            loadVouchers();
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }

    }

    //add images and descriptions to respective arrayLists
    private void initBitmapsUpcomingEvents(String mUrls, String mDescriptions) {
        //test events
        Event newEvent = new Event();
        newEvent.locationString = mDescriptions;
        newEvent.imageUrl = mUrls;
        mEvents.add(newEvent);
        mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void loadVouchers() {
        mRef = FirebaseDatabase.getInstance().getReference();
        mRefForEvent = mRef.child("events");

        mSavedEventsIDs = mCurrUser.getSavedEventsIDs();
        //mEvents = new ArrayList<>();
        mRefForEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //look through all restaurants
                for (DataSnapshot dsRestaurant : dataSnapshot.getChildren()) {
                    //iterate through all events at that restaurant
                    for (DataSnapshot dsEvent : dsRestaurant.getChildren()) {
                        //that event is saved, should be added to arrayList
                        if (mSavedEventsIDs.containsKey(dsEvent.getKey())) {
                            initBitmapsUpcomingEvents(dsEvent.child("imageUrl").getValue().toString(), dsEvent.child("locationString").getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mSize = mSavedEventsIDs.size();
    }
}