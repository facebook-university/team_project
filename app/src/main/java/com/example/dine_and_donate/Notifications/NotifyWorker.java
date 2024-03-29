package com.example.dine_and_donate.Notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.dine_and_donate.Activities.LoginActivity;
import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.Notification;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.YelpService;
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
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotifyWorker extends Worker {

    private Event mEventToday = null;
    private Notification mNewNotification;
    private DatabaseReference mRef;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mFbUser;
    private DatabaseReference mNotificationRef;
    private Integer mCounter = 0;

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

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
        YelpService.findRestaurants(longitude, latitude, "distance", "50", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    final JSONArray restaurantsNearbyJSON = new JSONObject(jsonData).getJSONArray("businesses");
                    //add marker to each restaurant nearby
                    while (mCounter < restaurantsNearbyJSON.length()) {
                        final JSONObject restaurantJSON = restaurantsNearbyJSON.getJSONObject(mCounter);
                        final String yelpID = restaurantJSON.getString("id");
                        final String restaurantName = restaurantJSON.getString("name");
                        final String latitude = restaurantJSON.getJSONObject("coordinates").getString("latitude");
                        final String longitude = restaurantJSON.getJSONObject("coordinates").getString("longitude");

                        mDatabase = FirebaseDatabase.getInstance();
                        mFbUser = FirebaseAuth.getInstance().getCurrentUser();
                        mRef = mDatabase.getReference();
                        mNotificationRef = mRef.child("users").child(mFbUser.getUid()).getRef().child("Notifications").push();
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
                                            String locationString = eventChild.child("locationString").getValue().toString();
                                            String info = eventChild.child("info").getValue().toString();
                                            Long startTime = (long) eventChild.child("startTime").getValue();
                                            String imageURL = eventChild.child("imageUrl").getValue().toString();
                                            String eventId = eventChild.child("eventId").getValue().toString();
                                            String orgId = eventChild.child("orgId").getValue().toString();
                                            mEventToday = new Event(orgId, yelpID, locationString, startTime, dateOfEvent, info, imageURL, eventId);
                                            displayNotification(latitude, longitude, eventChild.getKey(), timeNow.toString(), restaurantName);
                                            mCounter = restaurantsNearbyJSON.length();
                                            return;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                        mCounter++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void displayNotification(final String latitude, final String longitude, final String eventKey, final String createdAt, final String restaurantName) {
        final NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        final String NOTIFICATION_CHANNEL_ID = "com.example.dine_and_donate";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Dine&Donate Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        mRef.child("users").child(mEventToday.orgId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String orgName = dataSnapshot.child("name").getValue().toString();

                Intent seeEventDetails = new Intent(getApplicationContext(), LoginActivity.class);
                seeEventDetails.putExtra("latitude", latitude);
                seeEventDetails.putExtra("longitude", longitude);
                seeEventDetails.putExtra("defaultFragment", "map");

                int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);

                PendingIntent pendingIntentEvents = PendingIntent.getActivity(getApplicationContext(), uniqueInt, seeEventDetails, PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);

                notificationBuilder
                        .setDefaults(android.app.Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.notification_logo)
                        .setContentIntent(pendingIntentEvents)
                        .setContentTitle("Dine & Donate at " + restaurantName + " today!")
                        .setAutoCancel(true)
                        .setContentText("Come support " + orgName + " at " + MapFragment.getDate(mEventToday.startTime, "h:mm a"));

                notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef.child("users").child(mFbUser.getUid()).child("Notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNewNotification = new Notification(eventKey, mEventToday.getYelpID(), createdAt, mEventToday.orgId);
                mNotificationRef.setValue(mNewNotification);
                System.out.println("HERE");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}