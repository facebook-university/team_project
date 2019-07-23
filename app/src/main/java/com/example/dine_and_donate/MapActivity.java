package com.example.dine_and_donate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dine_and_donate.Listeners.OnSwipeTouchListener;
import com.example.dine_and_donate.Models.Restaurant;
import com.example.dine_and_donate.Models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import okhttp3.Call;
import okhttp3.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Response;
import okhttp3.ResponseBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class MapActivity extends AppCompatActivity {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = TimeUnit.SECONDS.toSeconds(6000);  /* 60 secs */
    private long FASTEST_INTERVAL = 50000; /* 5 secs */
    private String API_KEY = "AIzaSyBtH_PTSO3ou7pjuknEY-9HdTr3XhDJDeg";
    private final static String KEY_LOCATION = "location";
    public static final String TAG = MapActivity.class.getSimpleName();
    public JSONArray restaurantsNearbyJSON = new JSONArray();
    private boolean loaded;
    private Double cameraLatitude;
    private Double cameraLongitude;

    private View slideView;
    private boolean slideViewIsUp;
    private Button btnCreate;
    private boolean isOrg;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private User currentUserModel;

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        loaded = false;
        mAuth = FirebaseAuth.getInstance();
        isOrg = true;

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

        // Initialize Places.
        Places.initialize(getApplicationContext(), API_KEY);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Change bottom navigation map icon to filled
        bottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_filled_50);

        //Add click listener to bottom navigation bar for navigating between views
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_notify:
                        //Go to Notifications
                        navigationHelper(NotificationsActivity.class);
                        break;
                    case R.id.action_map:
                        break;
                    case R.id.action_profile:
                        //Go to Profile
                        navigationHelper(ProfileActivity.class);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // setting up slide view with restaurant info
        slideView = findViewById(R.id.slide_menu);
        slideView.setVisibility(View.INVISIBLE);
        slideView.setY(1200);
        slideViewIsUp = false;

        // slide view can be swiped down to dismiss and swiped up for more info
        slideView.setOnTouchListener(new OnSwipeTouchListener(MapActivity.this) {
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

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Double newLongitude = map.getCameraPosition().target.longitude;
                Double newLatitude = map.getCameraPosition().target.latitude;
                if (cameraLatitude == null || cameraLongitude == null) {
                    cameraLatitude = newLatitude;
                    cameraLongitude = newLongitude;
                }

                if (((Math.abs(newLongitude - cameraLongitude) > 0.03)
                    || (Math.abs(newLatitude - cameraLatitude) > 0.03))
                    && (map.getCameraPosition().zoom > 10)) {
                    cameraLongitude = newLongitude;
                    cameraLatitude = newLatitude;
                }

                generateMarkers(Double.toString(cameraLongitude), Double.toString(cameraLatitude));
            }
        });

        if (map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            MapDemoActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            MapDemoActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);

            //map.setOnMapLongClickListener(this);
            map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));

            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapDemoActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        map.setMyLocationEnabled(true);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
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

    // Called when the Activity becomes visible.
    @Override
    protected void onStart() {
        super.onStart();
    }

    // Called when the Activity is no longer visible.
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCurrentLocation != null) {
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        MapDemoActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                        if(!loaded) {
                            LatLng currLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                            map.moveCamera(CameraUpdateFactory.newLatLng(currLatLng));
                            map.animateCamera(CameraUpdateFactory.zoomTo(15));
                            loaded = true;
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
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void navigationHelper(Class activity) {
        final Intent loginToTimeline = new Intent(this, activity);
        startActivity(loginToTimeline);
    }

    private void generateMarkers(String longitude, String latitude) {
        final YelpService yelpService = new YelpService();
        YelpService.findRestaurants(longitude, latitude, new Callback() {
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
                                    if (snapshot.exists() || isOrg) {
                                        MapActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                map.addMarker(new MarkerOptions().position(restaurantPosition).title(restaurantName)).setTag(finalI);
                                                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                    @Override
                                                    public boolean onMarkerClick(Marker marker) {
                                                        try {
                                                            slideUpMenu(restaurantsNearbyJSON.getJSONObject((Integer) marker.getTag()));
                                                            slideViewIsUp = true;
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
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, EventActivity.class);
                try {
                    intent.putExtra("location", Restaurant.format(restaurant));
                    JSONObject restLocation = restaurant.getJSONObject("coordinates");
                    intent.putExtra("yelpID", restaurant.getString("id"));
                    intent.putExtra("isOrg", isOrg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    private void slideDownMenu() {
        slideView.setVisibility(View.INVISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                slideView.getY());
        animate.setDuration(500);
        animate.setFillAfter(true);
        slideView.startAnimation(animate);
    }
}