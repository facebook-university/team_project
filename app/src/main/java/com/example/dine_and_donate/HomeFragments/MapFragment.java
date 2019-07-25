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

import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.EventActivity;
import com.example.dine_and_donate.Listeners.OnSwipeTouchListener;
import com.example.dine_and_donate.Models.Restaurant;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MapFragment extends Fragment {

    private long UPDATE_INTERVAL = TimeUnit.SECONDS.toSeconds(6000);  /* 60 secs */
    private long FASTEST_INTERVAL = 50000; /* 5 secs */
    private String API_KEY = "AIzaSyBtH_PTSO3ou7pjuknEY-9HdTr3XhDJDeg";
    private final static String KEY_LOCATION = "location";
    public static final String TAG = MapFragment.class.getSimpleName();
    public JSONArray restaurantsNearbyJSON = new JSONArray();

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private boolean mLoaded;
    private boolean mCameraSet;
    private Double mCameraLatitude;
    private Double mCameraLongitude;

    private View mSlideView;
    private boolean mSlideViewIsUp;
    private Button mBtnCreate;
    private boolean mIsOrg;

    private Context mContext;

    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

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
        mLoaded = false;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HomeActivity homeActivity = (HomeActivity) getActivity();
        mIsOrg = homeActivity.mCurrentUser.isOrg;

        mAuth = FirebaseAuth.getInstance();
        mContext = view.getContext();

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (!mLoaded) {
            mMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            if (mMapFragment != null) {
                mMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);
                    }
                });
                mLoaded = true;
            } else {
                Toast.makeText(mContext, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
            }
        }

        // Initialize Places.
        Places.initialize(mContext.getApplicationContext(), API_KEY);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(mContext);

        // setting up slide view with restaurant info
        mSlideView = view.findViewById(R.id.slide_menu);
        mSlideView.setVisibility(View.INVISIBLE);
        mSlideView.setY(1200);
        mSlideViewIsUp = false;

        // slide view can be swiped down to dismiss and swiped up for more info
        mSlideView.setOnTouchListener(new OnSwipeTouchListener(mContext) {
            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                if(mSlideViewIsUp) {
                    slideDownMenu();
                }
            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                if(mSlideViewIsUp) {
                    //TODO: show more detail in view

                }
            }
        });

        mSlideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: same as swipe up
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void loadMap(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Double newLongitude = mMap.getCameraPosition().target.longitude;
                Double newLatitude = mMap.getCameraPosition().target.latitude;
                if (mCameraLatitude == null || mCameraLongitude == null) {
                    mCameraLatitude = newLatitude;
                    mCameraLongitude = newLongitude;
                }

                if (((Math.abs(newLongitude - mCameraLongitude) > 0.03)
                        || (Math.abs(newLatitude - mCameraLatitude) > 0.03))
                        && (mMap.getCameraPosition().zoom > 10)) {
                    mCameraLongitude = newLongitude;
                    mCameraLatitude = newLatitude;
                }

                generateMarkers(Double.toString(mCameraLongitude), Double.toString(mCameraLatitude));
            }
        });

        if (mMap != null) {
            // Map is ready
            Toast.makeText(mContext, "Map Fragment was mLoaded properly!", Toast.LENGTH_SHORT).show();
            MapDemoFragmentPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            MapDemoFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);

            mMap.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));

            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else {
            Toast.makeText(mContext, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
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
    void getMyLocation() {
        mMap.setMyLocationEnabled(true);

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
                        if(!mCameraSet) {
                            LatLng currLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currLatLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            mCameraSet = true;
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

    private void generateMarkers(String longitude, String latitude) {
        final YelpService yelpService = new YelpService();
        yelpService.findRestaurants(longitude, latitude, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    try {
                        restaurantsNearbyJSON = new JSONObject(jsonData).getJSONArray("businesses");
                        //add marker to each restaurant nearby
                        for (int i = 0; i < restaurantsNearbyJSON.length(); i++) {
                            final JSONObject restaurantJSON = restaurantsNearbyJSON.getJSONObject(i);
                            String yelpID = restaurantJSON.getString("id");
                            System.out.println("ID: " + yelpID + " " + restaurantJSON.getString("name") + "\n");
                            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference mRef = mDatabase.getReference();
                            DatabaseReference ref = mRef.child("events");
                            JSONObject restLocation = restaurantJSON.getJSONObject("coordinates");
                            final String restaurantName = restaurantsNearbyJSON.getJSONObject(i).getString("name");
                            final LatLng restaurantPosition = new LatLng(restLocation.getDouble("latitude"), restLocation.getDouble("longitude"));
                            final int finalI = i;

                            ref.child(yelpID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.exists() || mIsOrg) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mMap.addMarker(new MarkerOptions().position(restaurantPosition).title(restaurantName)).setTag(finalI);
                                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                    @Override
                                                    public boolean onMarkerClick(Marker marker) {
                                                        try {
                                                            slideUpMenu(restaurantsNearbyJSON.getJSONObject((Integer) marker.getTag()));
                                                            mSlideViewIsUp = true;
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        return false;
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void slideUpMenu(final JSONObject restaurant) throws JSONException {
        TextView restName = mSlideView.findViewById(R.id.tv_restaurant_name);
        restName.setText(restaurant.getString("name"));
        mSlideView.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                mSlideView.getY(),
                0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        mSlideView.startAnimation(animate);

        mBtnCreate = mSlideView.findViewById(R.id.btn_create_event);
        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventActivity.class);
                try {
                    intent.putExtra("location", Restaurant.format(restaurant));
                    JSONObject restLocation = restaurant.getJSONObject("coordinates");
                    intent.putExtra("yelpID", restaurant.getString("id"));
                    intent.putExtra("mIsOrg", mIsOrg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    private void slideDownMenu() {
        mSlideView.setVisibility(View.INVISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                mSlideView.getY());
        animate.setDuration(500);
        animate.setFillAfter(true);
        mSlideView.startAnimation(animate);
    }

}
