package com.example.dine_and_donate.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Adapters.EventListViewAdapter;
import com.example.dine_and_donate.Adapters.RestaurantListViewAdapter;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.R;

import org.json.JSONArray;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    private RestaurantListViewAdapter mOrgAdapter;
    private EventListViewAdapter mUserAdapter;
    private ArrayList<Event> mNearbyEvents;
    private JSONArray mRestaurantsJSON;
    private RecyclerView mRvNearbyList;
    private HomeActivity mActivity;
    private int mSize;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mSize = 0;
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOrgAdapter = new RestaurantListViewAdapter(mRestaurantsJSON);
        mUserAdapter = new EventListViewAdapter(mNearbyEvents);
        mRvNearbyList = view.findViewById(R.id.rvNearbyList);
        mRvNearbyList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mActivity = (HomeActivity) getActivity();

        if(mActivity.currentUser.isOrg) {
            mRvNearbyList.setAdapter(mOrgAdapter);
        } else {
            // todo: fill rv with event info
            if(mSize != mUserAdapter.getItemCount()) {
                mRvNearbyList.setAdapter(mUserAdapter);
                mSize = mUserAdapter.getItemCount();
            }
        }
    }

    public void setNearbyEvents(ArrayList<Event> mNearbyEvents) {
        this.mNearbyEvents = mNearbyEvents;
    }

    public void setRestaurantsJSON(JSONArray mRestaurantsJSON) {
        this.mRestaurantsJSON = mRestaurantsJSON;
    }
}
