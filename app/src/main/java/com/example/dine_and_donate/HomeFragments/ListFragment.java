package com.example.dine_and_donate.HomeFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Adapters.EventListViewAdapter;
import com.example.dine_and_donate.Adapters.RestaurantListViewAdapter;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.SearchDialogFragment;
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
    private DialogFragment mDialogFragment;
    private HomeActivity mActivity;
    private Location mLocation;
    private String queryOrgId;

    private DataSnapshot mAllEvents;
    private HashMap<String, JSONObject> mIdToRestaurant;
    private HashMap<String, User> mIdToOrg;
    private ArrayList<String> orgNames;
    private HashMap<String, String> orgNameToId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (HomeActivity) getActivity();
        if (mActivity.currentUser.isOrg) {
            mOrgAdapter = new RestaurantListViewAdapter(mRestaurantsJSON, mLocation);
        } else {
            mUserAdapter = new EventListViewAdapter(mAllEvents,mIdToRestaurant, mIdToOrg, queryOrgId);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvNearbyList = view.findViewById(R.id.rvNearbyList);
        mRvNearbyList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        if (mActivity.currentUser.isOrg) {
            mOrgAdapter.notifyDataSetChanged();
            mRvNearbyList.setAdapter(mOrgAdapter);
        } else {
            if (mUserAdapter.getItemCount() != 0) {
                mUserAdapter.notifyDataSetChanged();
                mRvNearbyList.setAdapter(mUserAdapter);
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

    public void setLocation(Location mLocation) {
        this.mLocation = mLocation;
    }

    public void setQueryOrgId(String id) {
        this.queryOrgId = id;
    }

    public void setOrgNames(ArrayList<String> orgNames) {
        this.orgNames = orgNames;
    }
}
