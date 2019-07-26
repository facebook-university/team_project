package com.example.dine_and_donate.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.R;

import org.json.JSONArray;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    private ArrayList<Event> mNearbyEvents;
    private JSONArray mRestaurantsJSON;
    private RecyclerView mRvNearbyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvNearbyList = view.findViewById(R.id.rvNearbyList);
        if(mNearbyEvents.size() == 0) {
            // todo: fill rv with restaurant info
        } else {
            // todo: fill rv with event info
        }

    }

    public void setmNearbyEvents(ArrayList<Event> mNearbyEvents) {
        this.mNearbyEvents = mNearbyEvents;
    }

    public void setmRestaurantsJSON(JSONArray mRestaurantsJSON) {
        this.mRestaurantsJSON = mRestaurantsJSON;
    }
}
