package com.example.dine_and_donate.HomeFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Activities.EventActivity;
import com.example.dine_and_donate.Listeners.OnSwipeTouchListener;

import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.Restaurant;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.YelpService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.goodiebag.carouselpicker.CarouselPicker;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MapFragment extends Fragment {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = TimeUnit.SECONDS.toSeconds(6000);  /* 60 secs */
    private long FASTEST_INTERVAL = 50000; /* 5 secs */
    private String API_KEY = "AIzaSyBtH_PTSO3ou7pjuknEY-9HdTr3XhDJDeg";
    private final static String KEY_LOCATION = "location";

    private boolean loaded;
    private int position = 0;
    private boolean cameraSet;
    private Double cameraLatitude;
    private Double cameraLongitude;

    private View slideView;
    private boolean slideViewIsUp;
    private Button btnCreate;

    private Context mContext;

    private FirebaseDatabase mDatabase;
    private FirebaseUser mFbUser;
    private DatabaseReference mRef;
    private DatabaseReference mRefForUser;
    private DatabaseReference mRefForEvents;
    private DatabaseReference mRefForOrgs;
    private User mCurrentUser;

    private HomeActivity homeActivity;

    public Location mCurrentLocation;

    // These 3 fields are passed to list fragment
    public JSONArray restaurantsNearbyJSON = new JSONArray();
    private DataSnapshot mAllEvents;
    private HashMap<String, JSONObject> mIdToRestaurant = new HashMap<>();
    private HashMap<String, User> mIdToOrg = new HashMap<>();

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaded = false;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        homeActivity = (HomeActivity) getActivity();
        mCurrentUser = homeActivity.currentUser;


        mDatabase = FirebaseDatabase.getInstance();
        mFbUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = mDatabase.getReference(); //need an instance of database reference
        mRefForUser = mRef.child("users").child(mFbUser.getUid());
        mRefForEvents = mRef.child("events");
        mRefForOrgs = mRef.child("users");
        mContext = view.getContext();

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (!loaded) {
            mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);
                    }
                });
                loaded = true;
            } else {
                Toast.makeText(mContext, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
            }
        }

        // Initialize Places.
        Places.initialize(mContext.getApplicationContext(), API_KEY);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(mContext);

        // setting up slide view with restaurant info
        slideView = view.findViewById(R.id.slide_menu);
        slideView.setVisibility(View.INVISIBLE);
        slideViewIsUp = false;

        // slide view can be swiped down to dismiss and swiped up for more info
        slideView.setOnTouchListener(new OnSwipeTouchListener(mContext) {
            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                if(slideViewIsUp) {
                    slideDownMenu();
                }
            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                if(slideViewIsUp) {
                    //TODO: show more detail in view

                }
            }
        });

        slideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: same as swipe up
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void loadMap(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Double newLongitude = map.getCameraPosition().target.longitude;
                Double newLatitude = map.getCameraPosition().target.latitude;
                if (cameraLatitude == null || cameraLongitude == null) {
                    cameraLatitude = newLatitude;
                    cameraLongitude = newLongitude;
                    generateMarkers();
                }

                if (map.getCameraPosition().zoom > 10) {
                    cameraLongitude = newLongitude;
                    cameraLatitude = newLatitude;
                    generateMarkers();
                }
            }
        });

        if (map != null) {
            // Map is ready
            Toast.makeText(mContext, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            MapDemoFragmentPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            MapDemoFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);

            //map.setOnMapLongClickListener(this);
            map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));

            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else {
            Toast.makeText(mContext, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateMarkers() {
        if (mCurrentUser.isOrg) {
            generateMarkersRestaurants(Double.toString(cameraLongitude), Double.toString(cameraLatitude));
        } else {
            generateMarkersEvents();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapDemoFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void getMyLocation() {
        map.setMyLocationEnabled(true);

        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(mContext);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(mContext).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                        if(!cameraSet) {
                            LatLng initialLatLng = homeActivity.getMarkerLatLng() != null ? homeActivity.markerLatLng
                                    : new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                            map.moveCamera(CameraUpdateFactory.newLatLng(initialLatLng));
                            map.animateCamera(CameraUpdateFactory.zoomTo(15));
                            cameraSet = true;
                        }
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        mCurrentLocation = location;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
    }

    private void generateMarkersRestaurants(String longitude, String latitude) {
        final YelpService yelpService = new YelpService();
        yelpService.findRestaurants(longitude, latitude, "best_match", "30", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    restaurantsNearbyJSON = new JSONObject(jsonData).getJSONArray("businesses");
                    //add marker to each restaurant nearby
                    for (int i = 0; i < restaurantsNearbyJSON.length(); i++) {
                        final JSONObject restaurantJSON = restaurantsNearbyJSON.getJSONObject(i);
                        final JSONObject restLocation = restaurantJSON.getJSONObject("coordinates");
                        final String restaurantName = restaurantsNearbyJSON.getJSONObject(i).getString("name");
                        final LatLng restaurantPosition = new LatLng(restLocation.getDouble("latitude"), restLocation.getDouble("longitude"));

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                map.addMarker(new MarkerOptions().position(restaurantPosition).title(restaurantName)).setTag(restaurantJSON);
                                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        slideUpMenuCreate((JSONObject) marker.getTag());
                                        slideViewIsUp = true;
                                        return false;
                                    }
                                });
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void generateMarkersEvents() {
        final YelpService yelpService = new YelpService();

        mRefForEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mAllEvents = dataSnapshot;
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for(DataSnapshot event : snapshot.getChildren()) {
                        saveOrg(event.child("orgId").getValue().toString());
                    }
                    yelpService.findRestaurants(snapshot.getKey(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String jsonData = response.body().string();
                            try {
                                final JSONObject restaurantJSON = new JSONObject(jsonData);

                                mIdToRestaurant.put(restaurantJSON.getString("id"), restaurantJSON);

                                final JSONObject restLocation = restaurantJSON.getJSONObject("coordinates");
                                final String restaurantName = restaurantJSON.getString("name");
                                final LatLng restaurantPosition = new LatLng(restLocation.getDouble("latitude"), restLocation.getDouble("longitude"));

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Marker marker = map.addMarker(new MarkerOptions().position(restaurantPosition).title(restaurantName));
                                        marker.setTag(restaurantJSON);
                                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(Marker marker) {
                                                return clickMarker(marker);
                                            }
                                        });
                                        if (marker.getPosition().equals(homeActivity.getMarkerLatLng())) {
                                            clickMarker(marker);
                                            homeActivity.setMarkerLatLngToNull();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private boolean clickMarker(final Marker marker) {
        try {
            final JSONObject restaurantOfMarker = (JSONObject) marker.getTag();
            mRefForEvents.child(restaurantOfMarker.getString("id"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            slideUpMenuSave(restaurantOfMarker, dataSnapshot);
                            marker.showInfoWindow();
                            slideViewIsUp = true;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void slideUpAnimation(final JSONObject restaurant) {
        try {
            TextView restName = slideView.findViewById(R.id.tv_restaurant_name);
            restName.setText(restaurant.getString("name"));
            slideView.setVisibility(View.VISIBLE);
            TranslateAnimation animate = new TranslateAnimation(
                    0,
                    0,
                    slideView.getY(),
                    0);
            animate.setDuration(500);
            animate.setFillAfter(true);
            slideView.startAnimation(animate);
            btnCreate = slideView.findViewById(R.id.btn_create_event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void slideUpMenuCreate(final JSONObject restaurant) {
        slideUpAnimation(restaurant);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventActivity.class);
                try {
                    intent.putExtra("location", Restaurant.format(restaurant));
                    JSONObject restLocation = restaurant.getJSONObject("coordinates");
                    intent.putExtra("yelpID", restaurant.getString("id"));
                    intent.putExtra("isOrg", mCurrentUser.isOrg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    private void slideUpMenuSave(final JSONObject restaurant, final DataSnapshot snapshot) {
        slideUpAnimation(restaurant);
        setUpCarousel(snapshot);
        btnCreate.setText(getString(R.string.save));
    }

    private void slideDownMenu() {
        // todo: make not clickable when it goes away
        slideView.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                slideView.getY());
        animate.setDuration(500);
        animate.setFillAfter(true);
        slideView.startAnimation(animate);
    }


    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }


    public JSONArray getRestaurantsNearbyJSON() {
        return restaurantsNearbyJSON;
    }

    public DataSnapshot getAllEvents() {
        return mAllEvents;
    }

    public HashMap<String, JSONObject> getIdToRestaurant() {
        return mIdToRestaurant;
    }

    public HashMap<String, User> getIdToOrg() {
        return mIdToOrg;
    }

    private void setUpCarousel(final DataSnapshot snapshot) {
        CarouselPicker carouselPicker = (CarouselPicker) slideView.findViewById(R.id.carousel);

        final ArrayList<String> eventIDs = new ArrayList<String>();
        List<CarouselPicker.PickerItem> mixItems = new ArrayList<>();

        for (DataSnapshot eventChild : snapshot.getChildren()) {
            mixItems.add(new CarouselPicker.TextItem(getDate((long) eventChild.child("startTime").getValue(),
                    "MMM dd, yyyy \n hh:mm"),
                    5));
            eventIDs.add(eventChild.getKey());
        }

        CarouselPicker.CarouselViewAdapter mixAdapter = new CarouselPicker.CarouselViewAdapter(slideView.getContext(), mixItems, 0);
        carouselPicker.setAdapter(mixAdapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, String> savedEvents = mCurrentUser.getSavedEventsIDs();
                savedEvents.put(eventIDs.get(position), snapshot.getKey());
                mRefForUser.child("Events").setValue(savedEvents, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Toast.makeText(getContext(), getString(R.string.errorMsg), Toast.LENGTH_LONG).show();
                        } else {
                            btnCreate.setText(getString(R.string.saved));
                            mCurrentUser.addSavedEventID(savedEvents);
                        }
                    }
                });
            }
        });

        carouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                btnCreate.setText(getString(R.string.save));
            }

            @Override
            public void onPageSelected(final int i) {
                position = i;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void saveOrg(final String id) {
        mRefForOrgs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mIdToOrg.put(id, dataSnapshot.child(id).getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}