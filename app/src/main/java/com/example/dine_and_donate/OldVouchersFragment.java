package com.example.dine_and_donate;

import android.content.Context;
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
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;

public class OldVouchersFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;
    private TabFragmentHelper mTabFragmentHelper;
    private Context mContext;
    private ArrayList<Event> mEvents = new ArrayList<>();
    private TextView mEmptyView;
    private HomeActivity mHomeActivity;
    private HashSet<String> mAlreadyLoaded;

    //inflates layout of fragment
    private Map<String, String> pastEvents;
    private User mCurrUser;
    private DatabaseReference mRef;
    private DatabaseReference mRefForEvent;
    private StaggeredRecyclerViewAdapter mStaggeredRecyclerViewAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeActivity = (HomeActivity) getActivity();
        mAlreadyLoaded = new HashSet<>();
    }

    //inflates layout of fragment
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        mCurrUser = homeActivity.currentUser;
        pastEvents = mCurrUser.getSavedEventsIDs();

        mView = inflater.inflate(R.layout.tab_fragment, container, false);
        mRecyclerView = mView.findViewById(R.id.rv_vouchers);
        mEmptyView = mView.findViewById(R.id.empty_view);
        mStaggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(getActivity(), mEvents);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mStaggeredRecyclerViewAdapter);
        mTabFragmentHelper = new TabFragmentHelper(mEvents, mStaggeredRecyclerViewAdapter, true);
//        super.onViewCreated(mView, savedInstanceState);
        if (pastEvents.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mView, savedInstanceState);
        pastEvents = mCurrUser.getSavedEventsIDs();
        if (mStaggeredRecyclerViewAdapter.getItemCount() == 0 || mHomeActivity.isNewSavedEvent()) {
            loadVouchers();
            mHomeActivity.setNewSavedEvent(false);
        }
    }

    public void loadVouchers() {
        mRef = FirebaseDatabase.getInstance().getReference();
        mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
        mRefForEvent = mRef.child("events");
        mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
        pastEvents = mCurrUser.getSavedEventsIDs();
        mRefForEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //look through all restaurants
                for (DataSnapshot dsRestaurant : dataSnapshot.getChildren()) {
                    //iterate through all events at that restaurant
                    for (DataSnapshot dsEvent : dsRestaurant.getChildren()) {
                        //that event is saved, should be added to arrayList
                        if (pastEvents.containsKey(dsEvent.getKey()) && !mAlreadyLoaded.contains(dsEvent.getKey())) {
                            long todayMillis = Calendar.getInstance().getTimeInMillis();
                            long eventEndMillis = Long.parseLong(dsEvent.child("endTime").getValue().toString());
                            //if event end date is older than today's date, it is a past event
                            if(todayMillis > eventEndMillis) {
                                mTabFragmentHelper.initBitmapsEvents(dsEvent.child("imageUrl").getValue().toString(), dsEvent.child("locationString").getValue().toString());
                                mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
                                mAlreadyLoaded.add(dsEvent.getKey());
                            }
//                            mTabFragmentHelper.initBitmapsEvents(dsEvent.child("imageUrl").getValue().toString(), dsEvent.child("locationString").getValue().toString(), todayMillis, eventEndMillis);
//                            mStaggeredRecyclerViewAdapter.notifyDataSetChanged();
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