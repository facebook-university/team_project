package com.example.dine_and_donate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dine_and_donate.LoginActivity;
import com.example.dine_and_donate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {
    MenuItem createEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Change bottom navigation profile icon to filled
        bottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_filled_24);

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
                        //Go to Map
                        navigationHelper(MapActivity.class);
                        break;
                    case R.id.action_profile:
                        break;
                    default: break;
                }
                return true;
            }
        });
    }

    private void navigationHelper(Class activity) {
        final Intent loginToTimeline = new Intent(this, activity);
        startActivity(loginToTimeline);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        createEvent = menu.findItem(R.id.createEvent);
        createEvent.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                createEvent();
                return false;
            }
        });


        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Associate searchable configuration with the SearchView
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void createEvent() {
        System.out.println("Clicked on Create Event!");
        //TO DO
    }
}
