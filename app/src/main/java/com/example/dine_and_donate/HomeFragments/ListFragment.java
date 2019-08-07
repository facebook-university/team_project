package com.example.dine_and_donate.HomeFragments;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
        if(mActivity.currentUser.isOrg) {
            mOrgAdapter = new RestaurantListViewAdapter(mRestaurantsJSON, mLocation);
        } else {
            setHasOptionsMenu(true);
            mUserAdapter = new EventListViewAdapter(mAllEvents,mIdToRestaurant, mIdToOrg, queryOrgId);
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
                mUserAdapter.notifyDataSetChanged();
                mRvNearbyList.setAdapter(mUserAdapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.activity_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchEvents:
                searchOrgs();
                return true;
            default:
                break;
        }
        return false;
    }

    public void searchOrgs() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.search, null);
        Button searchOrgsButton = view.findViewById(R.id.search_btn);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, orgNames);
        final AutoCompleteTextView searchQuery = (AutoCompleteTextView)
                view.findViewById(R.id.autoCompleteSearchOrg);
        searchQuery.setAdapter(adapter);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        searchOrgsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchQuery.getText().toString();
                queryOrgId = orgNameToId.get(query);
                dialog.dismiss();
                mUserAdapter = new EventListViewAdapter(mAllEvents,mIdToRestaurant, mIdToOrg, queryOrgId);
                mUserAdapter.notifyDataSetChanged();
                mRvNearbyList.setAdapter(mUserAdapter);
                queryOrgId = null;
            }
        });

        dialog.show();
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

    public void setOrgNameToId(HashMap<String, String> orgNameToId) {
        this.orgNameToId = orgNameToId;
    }
}
