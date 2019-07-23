package com.example.dine_and_donate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.dine_and_donate.Models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    //elements in layout
    private TabLayout tabLayout;
    private ViewPagerAdadpter voucherPagerAdadpter;
    private ViewPager voucherView;

    private TextView userName;
    private TextView bio;
    private ImageView profPic;
    private ImageView blurredPic;

    private ViewFlipper viewFlipper;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle aToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private User currentUserModel;
    private BottomNavigationView bottomNavigationView;
    private DatabaseReference mDatabase;
    private FirebaseUser fbUser;

    private boolean isOrg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

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
                 }
                 return true;
             }
         });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Change bottom navigation profile icon to filled
        bottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_filled_24);

        setUpBottomNav();
        setUpTopProfile();
    }

    private void setUpTopProfile() {
        //set up for top of profile page
        viewFlipper = findViewById(R.id.viewFlipper);
        userName = findViewById(R.id.et_name);

        if (isOrg) {
            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.forOrg)));
        } else {
            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.forConsumer)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Associate searchable configuration with the SearchView
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void navigationHelper(Class activity) {
        final Intent loginToTimeline = new Intent(this, activity);
        // loginToTimeline.putExtra("isOrg", currentUserModel.getIsOrg());
        startActivity(loginToTimeline);
    }

    private void setUpBottomNav() {
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
}