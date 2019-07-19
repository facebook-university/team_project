package com.example.dine_and_donate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class ProfileActivity extends AppCompatActivity {

    //elements in layout
    private TabLayout tabLayout;
    private ViewPagerAdadpter viewPagerAdadpter;
    private ViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        tabLayout = findViewById(R.id.tabs_profile);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        viewPagerAdadpter = new ViewPagerAdadpter(getSupportFragmentManager());

       //Add Fragment Here
        viewPagerAdadpter.AddFragment(new Tab1Fragment(), "tab 1 fragment");
        viewPagerAdadpter.AddFragment(new Tab2Fragment(), "tab 2 fragment");
        viewPager.setAdapter(viewPagerAdadpter);
        tabLayout.setupWithViewPager(viewPager);


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