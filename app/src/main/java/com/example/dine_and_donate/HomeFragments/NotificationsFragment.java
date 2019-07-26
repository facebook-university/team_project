package com.example.dine_and_donate.HomeFragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.Notification;
import com.example.dine_and_donate.NotificationsAdapter;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.YelpService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotificationsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NotificationsAdapter mNotificationsAdapter;
    private List<Notification> mNotificationList;

    private FirebaseUser mFbUser;
    private DatabaseReference mRef;
    private DatabaseReference mRefForEvent;
    private JSONArray restaurantsNearbyJSON = new JSONArray();

    private boolean loaded;
    private boolean cameraSet;
    private Double cameraLatitude;
    private Double cameraLongitude;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Context mContext;
    private ArrayList<Event> allEvents;

    private Location mCurrentLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allEvents = new ArrayList<>();
        loaded = false;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRef = FirebaseDatabase.getInstance().getReference();
        mRefForEvent = mRef.child("events");

        mRecyclerView = view.findViewById(R.id.notifications_rv);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mNotificationList = new ArrayList<>();
        mNotificationsAdapter = new NotificationsAdapter(mNotificationList);
        mRecyclerView.setAdapter(mNotificationsAdapter);

//        mRefForEvent.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                JSONArray JSONRestaurants = generateMarkers(Double.toString(mCurrentLocation.getLongitude()), Double.toString(mCurrentLocation.getLatitude()));
//
//                for(int i = 0; i < JSONRestaurants.length(); i++) {
//                    try {
//                        Log.d("events", JSONRestaurants.getJSONObject(i).toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                try {
//                    allEvents = nearbyRestaurants(JSONRestaurants, dataSnapshot);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d("events", "canceled");
//            }
//        });
//
//        for(int i = 0; i < allEvents.size(); i++) {
//            Log.d("events", allEvents.get(i).toString());
//        }
        mNotificationList.clear();
        Collections.reverse(mNotificationList);
        mNotificationsAdapter.notifyDataSetChanged();
    }

    private JSONArray generateMarkers(String longitude, String latitude) {
        YelpService.findRestaurants(longitude, latitude, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    // This works
                    String jsonData = response.body().string();
                    Log.d("response", jsonData);
                    try {
                        // this never gets called
                        restaurantsNearbyJSON = new JSONObject(jsonData).getJSONArray("businesses");
                        Log.d("response", "gets here");
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return restaurantsNearbyJSON;
    }

    private ArrayList<Event> nearbyRestaurants(JSONArray array, DataSnapshot dataSnapshot) throws JSONException {
        ArrayList<Event> allEvents = new ArrayList<>();
        //iterate through all objects in JSONArray that has nearby restaurants
        for (int i = 0; i < array.length(); i++) {
            JSONObject restaurantObject = array.getJSONObject(i);
            String id = restaurantObject.getString("id");
            //iterate through children of events
            for (DataSnapshot restaurant : dataSnapshot.getChildren()) {
                //ID of restaurant in database matches the id of restaurant object in JSON Array
                if (restaurant.getKey().equals(id)) {
                    //iterate through children of restaurant that matches id of a nearby restaurant & add to array of events
                    for (DataSnapshot eventDs : restaurant.getChildren()) {
                        Event newEvent = eventDs.getValue(Event.class);
                        allEvents.add(newEvent);
                    }
                }
            }
        }
        return allEvents;
    }

    public void setmCurrentLocation(Location mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }
}