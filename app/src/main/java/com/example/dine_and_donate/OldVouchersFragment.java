package com.example.dine_and_donate;

import android.content.Context;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.Map;

public class OldVouchersFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;
    private TabFragmentHelper mTabFragmentHelper;
    private Context mContext;
    private ArrayList<Event> mEvents;
    private TextView mEmptyView;

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
        mEmptyView = mView.findViewById(R.id.empty_view);
        mRecyclerView = mView.findViewById(R.id.rv_vouchers);
        mStaggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(getActivity(), mEvents);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(mStaggeredRecyclerViewAdapter);
        mTabFragmentHelper = new TabFragmentHelper(mEvents, mStaggeredRecyclerViewAdapter, true);
        super.onViewCreated(mView, savedInstanceState);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        mCurrUser = homeActivity.currentUser;
        super.onViewCreated(mView, savedInstanceState);
        pastEvents = mCurrUser.getSavedEventsIDs();
        if (pastEvents.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        loadVouchers();
    }

    public void loadVouchers() {
        mRef = FirebaseDatabase.getInstance().getReference();
        mRefForEvent = mRef.child("events");
        mRefForEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //look through all restaurants
                for (DataSnapshot dsRestaurant : dataSnapshot.getChildren()) {
                    //iterate through all events at that restaurant
                    for (DataSnapshot dsEvent : dsRestaurant.getChildren()) {
                        //that event is saved, should be added to arrayList
                        if (pastEvents.containsKey(dsEvent.getKey())) {
                            long dateMillis = Calendar.getInstance().getTimeInMillis();
                            //if event end date is older than today's date, it is a past event
                            long otherMillis = Long.parseLong(dsEvent.child("endTime").getValue().toString());
                            mTabFragmentHelper.initBitmapsEvents(dsEvent.child("imageUrl").getValue().toString(), dsEvent.child("locationString").getValue().toString(), dateMillis, otherMillis);
                            mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}