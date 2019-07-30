package com.example.dine_and_donate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Activities.LoginActivity;
import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import org.parceler.Parcels;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotifyWorker extends Worker {
    private Event mEventToday = null;

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public Result doWork() {
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String longitude = Double.toString(location.getLongitude());
        String latitude = Double.toString(location.getLatitude());

        getEvent(longitude, latitude);
        return Result.success();
    }

    private void getEvent(String longitude, String latitude) {
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
                        JSONArray restaurantsNearbyJSON = new JSONObject(jsonData).getJSONArray("businesses");
                        //add marker to each restaurant nearby
                        for (int i = 0; i < restaurantsNearbyJSON.length(); i++) {
                            final JSONObject restaurantJSON = restaurantsNearbyJSON.getJSONObject(i);
                            final String yelpID = restaurantJSON.getString("id");
                            final String latitude = restaurantJSON.getJSONObject("coordinates").getString("latitude");
                            final String longitude = restaurantJSON.getJSONObject("coordinates").getString("longitude");

                            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference mRef = mDatabase.getReference();
                            DatabaseReference ref = mRef.child("events");

                            ref.child(yelpID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot eventChild : snapshot.getChildren()) {
                                            Long dateOfEvent = (long) eventChild.child("endTime").getValue();
                                            Long timeNow = System.currentTimeMillis();
                                            Long millisecondsToCheck = timeNow + (long) 60000 * 60 * 12;
                                            if (dateOfEvent >= timeNow && dateOfEvent <= millisecondsToCheck) {
                                                String orgId = eventChild.child("orgId").getValue().toString();
                                                String locationString = eventChild.child("locationString").getValue().toString();
                                                String info = eventChild.child("info").getValue().toString();
                                                Long startTime = (long) eventChild.child("startTime").getValue();
                                                String imageURL = eventChild.child("imageUrl").getValue().toString();
                                                mEventToday = new Event(orgId, yelpID, locationString, startTime, dateOfEvent, info, imageURL);
                                                displayNotification(mEventToday.locationString, mEventToday.info, latitude, longitude);
                                                return;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
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

    private void displayNotification(String title, String body, String latitude, String longitude) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.dine_and_donate";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Dine&Donate Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent seeEventDetails = new Intent(getApplicationContext(), LoginActivity.class);
        seeEventDetails.putExtra("latitude", latitude);
        seeEventDetails.putExtra("longitude", longitude);
        seeEventDetails.putExtra("defaultFragment", "map");

        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntentEvents = PendingIntent.getActivity(getApplicationContext(), uniqueInt, seeEventDetails, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);

        notificationBuilder
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.baker)
                .setContentIntent(pendingIntentEvents)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(body);

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }
}
