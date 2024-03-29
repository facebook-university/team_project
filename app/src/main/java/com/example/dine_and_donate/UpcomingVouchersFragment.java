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

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;

public class UpcomingVouchersFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Map<String, String> mSavedEventsIDs;
    private User mCurrUser;
    private DatabaseReference mRef;
    private DatabaseReference mRefForEvent;
    private View mView;
    private TextView mEmptyView;
    private TabFragmentHelper mTabFragmentHelper;
    private StaggeredRecyclerViewAdapter mStaggeredRecyclerViewAdapter;
    private ArrayList<Event> mEvents = new ArrayList<>();
    private HomeActivity mHomeActivity;
    private HashSet<String> mAlreadyLoaded;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeActivity = (HomeActivity) getActivity();
        mAlreadyLoaded = new HashSet<>();
    }

    //create view based on data in array lists, inflates the layout of the fragment
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        mCurrUser = homeActivity.currentUser;
        mSavedEventsIDs = mCurrUser.getSavedEventsIDs();
        mView = inflater.inflate(R.layout.tab_fragment, container, false);
        mRecyclerView = mView.findViewById(R.id.rv_vouchers);
        mEmptyView = mView.findViewById(R.id.empty_view);
        mStaggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(getActivity(), mEvents);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mStaggeredRecyclerViewAdapter);
        mTabFragmentHelper = new TabFragmentHelper(mEvents, mStaggeredRecyclerViewAdapter, false);
        mTabFragmentHelper.filterEvents();
        if (mSavedEventsIDs.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mView, savedInstanceState);
        mSavedEventsIDs = mCurrUser.getSavedEventsIDs();
        Log.d("size", mStaggeredRecyclerViewAdapter.getItemCount() + "");

        if (mStaggeredRecyclerViewAdapter.getItemCount() == 0 || mHomeActivity.isNewSavedEvent()) {
            loadVouchers();
            mHomeActivity.setNewSavedEvent(false);
        }
    }

    private void loadVouchers() {
        mRef = FirebaseDatabase.getInstance().getReference();
        mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
        mRefForEvent = mRef.child("events");
        mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
        mSavedEventsIDs = mCurrUser.getSavedEventsIDs();
        mRefForEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //look through all restaurants
                for (DataSnapshot dsRestaurant : dataSnapshot.getChildren()) {
                    //iterate through all events at that restaurant
                    for (DataSnapshot dsEvent : dsRestaurant.getChildren()) {
                        //that event is saved, should be added to arrayList
                        if (mSavedEventsIDs.containsKey(dsEvent.getKey())) {
                            //if event end date is older than today's date, it is a past event
                            long eventMillis = Long.parseLong(dsEvent.child("endTime").getValue().toString());
                            mTabFragmentHelper.initBitmapsEvents(dsEvent.child("imageUrl").getValue().toString(), dsEvent.child("locationString").getValue().toString(), eventMillis);
                        }
                    }
                }
                mTabFragmentHelper.filterEvents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}