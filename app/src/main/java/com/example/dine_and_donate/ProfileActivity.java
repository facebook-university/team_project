package com.example.dine_and_donate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    //elements in layout
    private TabLayout tabLayout;
    private ViewPagerAdadpter voucherPagerAdadpter;
    private ViewPager voucherView;

    //TODO populate these fields based on database information
    private TextView userName;
    private TextView bio;
    private ImageView profPic;
    private ImageView blurredPic;

    private ViewFlipper viewFlipper;
    private FirebaseDatabase mDatabase;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle aToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        mDatabase = FirebaseDatabase.getInstance();

        //set up for top of profile page
        viewFlipper = findViewById(R.id.viewFlipper);
        userName = findViewById(R.id.et_name);
        //TODO change boolean to isOrg boolean user attribute to display correct profile
        if(false) {
            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.forOrg)));
        } else {
            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.forConsumer)));
        }


        tabLayout = findViewById(R.id.tabs_profile);
        voucherView = (ViewPager) findViewById(R.id.viewpager_id);
        voucherPagerAdadpter = new ViewPagerAdadpter(getSupportFragmentManager());

       //Add Fragment Here
        voucherPagerAdadpter.AddFragment(new Tab1Fragment(), "tab 1 fragment");
        voucherPagerAdadpter.AddFragment(new Tab2Fragment(), "tab 2 fragment");

        voucherView.setAdapter(voucherPagerAdadpter);
        tabLayout.setupWithViewPager(voucherView);

        navigationView = (NavigationView) findViewById(R.id.settings_navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                 switch (menuItem.getItemId()) {
                     case R.id.shareFacebook:
                         Intent shareOnFB = new Intent (ProfileActivity.this, ShareEventActivity.class);
                         startActivity(shareOnFB);
                         return true;

                     case R.id.editProfile:
                             Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                             startActivity(intent);
                             return true;
                 }
                 return true;
             }
         });

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
}