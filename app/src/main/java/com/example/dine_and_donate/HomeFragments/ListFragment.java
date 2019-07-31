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
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.google.firebase.database.DataSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListFragment extends Fragment {

    private RestaurantListViewAdapter mOrgAdapter;
    private EventListViewAdapter mUserAdapter;
    private JSONArray mRestaurantsJSON;
    private RecyclerView mRvNearbyList;
    private HomeActivity mActivity;
    private int mSize;

    private DataSnapshot mAllEvents;
    private HashMap<String, JSONObject> mIdToRestaurant;
    private HashMap<String, User> mIdToOrg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mSize = 0;
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (HomeActivity) getActivity();
        if(mActivity.currentUser.isOrg) {
            mOrgAdapter = new RestaurantListViewAdapter(mRestaurantsJSON);
        } else {
            mUserAdapter = new EventListViewAdapter(mAllEvents,mIdToRestaurant, mIdToOrg);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvNearbyList = view.findViewById(R.id.rvNearbyList);
        mRvNearbyList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        if(mActivity.currentUser.isOrg) {
            mOrgAdapter.notifyDataSetChanged();
            mRvNearbyList.setAdapter(mOrgAdapter);
        } else {
            // todo: fill rv with event info
            if(mUserAdapter.getItemCount() != 0) {
                mRvNearbyList.setAdapter(mUserAdapter);
                mSize = mUserAdapter.getItemCount();
            }
        }
    }

    public void setRestaurantsJSON(JSONArray mRestaurantsJSON) {
        this.mRestaurantsJSON = mRestaurantsJSON;
    }

    public void setAllEvents(DataSnapshot mAllEvents) {
        this.mAllEvents = mAllEvents;
    }

    public void setIdToRestaurant(HashMap<String, JSONObject> mIdToRestaurant) {
        this.mIdToRestaurant = mIdToRestaurant;
    }

    public void setIdToOrg(HashMap<String, User> mIdToOrg) {
        this.mIdToOrg = mIdToOrg;
    }
}
