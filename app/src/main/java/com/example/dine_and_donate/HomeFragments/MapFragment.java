package com.example.dine_and_donate.HomeFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dine_and_donate.Activities.EventActivity;
import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.EventViewPagerAdapter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rd.PageIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MapFragment extends Fragment implements OnMapReadyCallback {

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
    private View slideViewContent;
    private ViewPager mViewPager;
    private boolean slideViewIsUp;
    private Button mBtnEvent;
    private EventViewPagerAdapter mPagerAdapter;

    private Context mContext;

    private FirebaseDatabase mDatabase;
    private FirebaseUser mFbUser;
    private DatabaseReference mRef;
    private DatabaseReference mRefForUser;
    private DatabaseReference mRefForEvents;
    private DatabaseReference mRefForOrgs;
    private User mCurrentUser;
    private ArrayList<Event> mNearbyEvents;
    private Map<String, String> mSavedEvents;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loaded = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeActivity = (HomeActivity) getActivity();
        mCurrentUser = homeActivity.currentUser;

        if (!loaded) {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            loaded = true;
        }

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

        // setting up slide view with restaurant info
        slideView = view.findViewById(R.id.slide_menu);
        ViewStub stub = slideView.findViewById(R.id.slide_up_stub);
        Integer slide_up_layout = (mCurrentUser.isOrg) ? R.layout.create_slide_up_fragment : R.layout.save_slide_up_fragment;
        stub.setLayoutResource(slide_up_layout);
        slideViewContent = stub.inflate();

        slideView.setVisibility(View.INVISIBLE);
        slideViewIsUp = false;

        // slide view can be swiped down to dismiss and swiped up for more info
        slideView.setOnTouchListener(new OnSwipeTouchListener(mContext) {
            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                if(slideViewIsUp) {
                    slideDownMenu(slideView);
                }
            }
        });
    }

    // GENERATE MARKERS //

    private void generateMarkersRestaurants(String longitude, String latitude) {
        final YelpService yelpService = new YelpService();
        YelpService.findRestaurants(longitude, latitude, "best_match", "30", new Callback() {
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
                    YelpService.findRestaurants(snapshot.getKey(), new Callback() {
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
            DataSnapshot eventsOfRestaurant = mAllEvents.child(restaurantOfMarker.getString("id"));

            slideUpMenuSave(restaurantOfMarker, eventsOfRestaurant);
            marker.showInfoWindow();

            slideViewIsUp = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    // SLIDE UP VIEW //

    private void slideUpAnimation(final JSONObject restaurant) {
        try {
            TextView restName = slideView.findViewById(R.id.tvRestaurantName);
            RatingBar rating = slideView.findViewById(R.id.ratingBar);
            TextView typeOfFood = slideView.findViewById(R.id.tvDescription);
            TextView milesAway = slideView.findViewById(R.id.tvDistance);

            String foodCategories;

            try {
                foodCategories = restaurant.getString("price") + " ∙ ";
            } catch (JSONException e) {
                foodCategories = "";
            }

            JSONArray categoriesJSONArray = restaurant.getJSONArray("categories");

            for (int i = 0; i < categoriesJSONArray.length(); i++) {
                foodCategories += categoriesJSONArray.getJSONObject(i).getString("title");
                if (i + 1 != categoriesJSONArray.length()) {
                    foodCategories += ", ";
                }
            }

            JSONObject coordinates = restaurant.getJSONObject("coordinates");
            String restLatitude = coordinates.getString("latitude");
            String restLongitude = coordinates.getString("longitude");

            milesAway.setText(getString(R.string.miles, distance(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude(),
                    Double.parseDouble(restLatitude),
                    Double.parseDouble(restLongitude))));

            restName.setText(restaurant.getString("name"));
            rating.setRating(Math.round(Double.parseDouble(restaurant.getString("rating"))));
            typeOfFood.setText(foodCategories);

            slideView.setVisibility(View.VISIBLE);
            TranslateAnimation animate = new TranslateAnimation(
                    0,
                    0,
                    slideView.getY(),
                    0);
            animate.setDuration(500);
            animate.setFillAfter(true);
            slideView.startAnimation(animate);
            mBtnEvent = slideViewContent.findViewById(R.id.btn_event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void slideUpMenuCreate(final JSONObject restaurant) {
        slideUpAnimation(restaurant);
        TextView phoneNumber = slideView.findViewById(R.id.phone_number);

        ImageView restaurantImg = slideView.findViewById(R.id.ivRestaurant);
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);

        try {
            phoneNumber.setText(restaurant.getString("display_phone"));

            Glide.with(mContext)
                    .load(restaurant.getString("image_url"))
                    .apply(requestOptions)
                    .into(restaurantImg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mBtnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventActivity.class);
                try {
                    intent.putExtra("location", Restaurant.format(restaurant));
                    intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCurrentUser));
                    JSONObject restLocation = restaurant.getJSONObject("coordinates");
                    intent.putExtra("yelpId", restaurant.getString("id"));
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
        setUpViewPager(snapshot);
    }

    public void slideDownMenu(View slider) {
        // todo: make not clickable when it goes away
        slider.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                slider.getY());
        animate.setDuration(500);
        animate.setFillAfter(false);
        slider.startAnimation(animate);
    }

    // VIEWPAGER

    @SuppressLint("ClickableViewAccessibility")
    private void setUpViewPager(final DataSnapshot snapshot) {
        final PageIndicatorView pageIndicatorView = slideViewContent.findViewById(R.id.pageIndicatorView);
        mSavedEvents = mCurrentUser.getSavedEventsIDs();
        mBtnEvent = slideViewContent.findViewById(R.id.btn_event);

        mViewPager = slideViewContent.findViewById(R.id.viewPager);
        pageIndicatorView.setSelection(0);
        mPagerAdapter = new EventViewPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);

        // Create an initial view to display; must be a subclass of FrameLayout.
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (DataSnapshot eventChild : snapshot.getChildren()) {
            FrameLayout event = (FrameLayout) inflater.inflate(R.layout.event_info_fragment, null);
            TextView eventOrg = event.findViewById(R.id.org);
            TextView eventInfo = event.findViewById(R.id.info);
            ImageView eventVoucher = event.findViewById(R.id.voucher_img);

            String date = getDate((long) eventChild.child("startTime").getValue(), "MMM dd, yyyy");
            String startTime = getDate((long) eventChild.child("startTime").getValue(), "h:mm a");
            String endTime = getDate((long) eventChild.child("endTime").getValue(), "h:mm a");
            User org = mIdToOrg.get((String) eventChild.child("orgId").getValue());

            eventOrg.setText(Html.fromHtml(getString(R.string.main_info_event,
                    org.name,
                    date,
                    startTime.replace("AM", "am").replace("PM","pm"),
                    endTime.replace("AM", "am").replace("PM","pm"))));

            eventInfo.setText(eventChild.child("info").getValue().toString());

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);

            Glide.with(mContext)
                    .load(eventChild.child("imageUrl").getValue().toString())
                    .apply(requestOptions)
                    .into(eventVoucher);

            mPagerAdapter.addView(event, eventChild.getKey());
            mPagerAdapter.notifyDataSetChanged();
        }

        saveButtonAtPosition(0, snapshot);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pagerPosition, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int pagerPosition) {
                pageIndicatorView.setSelection(pagerPosition);
                saveButtonAtPosition(pagerPosition, snapshot);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    private void saveButtonAtPosition(final int pagerPosition, final DataSnapshot snapshot) {
        if (mSavedEvents.get(mPagerAdapter.getEventId(pagerPosition)) != null) {
            mBtnEvent.setText(getString(R.string.saved));
        } else {
            mBtnEvent.setText(getString(R.string.save));
            mBtnEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String eventId = mPagerAdapter.getEventId(pagerPosition);
                    mSavedEvents.put(eventId, snapshot.getKey());
                    Log.d("newSize", mSavedEvents.size() + "");
                    mRefForUser.child("Events").setValue(mSavedEvents, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(getContext(), getString(R.string.errorMsg), Toast.LENGTH_LONG).show();
                            } else {
                                mBtnEvent.setText(getString(R.string.saved));
                                mCurrentUser.addSavedEventID(mSavedEvents);
                                mCurrentUser.savedEventsIDs = mSavedEvents;
                            }
                        }
                    });
                }
            });
        }
    }

    // LOAD MAP AND GET LOCATION //

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Double newLongitude = map.getCameraPosition().target.longitude;
                Double newLatitude = map.getCameraPosition().target.latitude;
                if (cameraLatitude == null || cameraLongitude == null) {
                    cameraLatitude = newLatitude;
                    cameraLongitude = newLongitude;

                    if (mCurrentUser.isOrg) {
                        generateMarkersRestaurants(Double.toString(cameraLongitude), Double.toString(cameraLatitude));
                    } else {
                        generateMarkersEvents();
                    }
                }

                if (map.getCameraPosition().zoom > 10) {
                    cameraLongitude = newLongitude;
                    cameraLatitude = newLatitude;

                    if (mCurrentUser.isOrg) {
                        generateMarkersRestaurants(Double.toString(cameraLongitude), Double.toString(cameraLatitude));
                    } else {
                        generateMarkersEvents();
                    }
                }
            }
        });

        if (map != null) {
            // Map is ready
            MapDemoFragmentPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            MapDemoFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);

            map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));

            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else {
            Toast.makeText(mContext, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MapDemoFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void getMyLocation() {
        map.setMyLocationEnabled(true);

        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(mContext);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(Location location) {
                        onLocationChanged(location);
                        if (location != null) {
                            if(!cameraSet) {
                                LatLng initialLatLng = homeActivity.getMarkerLatLng() != null ? homeActivity.markerLatLng
                                        : new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLng(initialLatLng));
                                map.animateCamera(CameraUpdateFactory.zoomTo(15));
                                cameraSet = true;
                            }
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
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    // HELPER FUNCTIONS //

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return "0";
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return new DecimalFormat("#.##").format(dist);
        }
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

    public Location getCurrentLocation() {
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
}