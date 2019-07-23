package com.example.dine_and_donate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.dine_and_donate.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class ProfileActivity extends AppCompatActivity {

    //elements in layout
    private TabLayout mTabLayout;
    private ViewPagerAdapter mVoucherPagerAdapter;
    private ViewPager mVoucherView;
    //TODO populate these fields based on database information
    private TextView mUserName;
    private TextView mBio;
    private ImageView mProfPic;
    private ImageView mBlurredPic;

    private FirebaseDatabase mDatabase;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle aToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private ConstraintLayout mLayoutForOrg;
    private ConstraintLayout mLayoutForConsumer;

    private User currentUserModel;
    private BottomNavigationView bottomNavigationView;
    private FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        mDatabase = FirebaseDatabase.getInstance();
        mLayoutForConsumer = findViewById(R.id.forConsumer);
        mLayoutForOrg = findViewById(R.id.forOrg);
        mTabLayout = findViewById(R.id.tabs_profile);
        mVoucherView = findViewById(R.id.viewpager_id);
        mVoucherPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mVoucherView.setAdapter(mVoucherPagerAdapter);
        mTabLayout.setupWithViewPager(mVoucherView);
        mUserName = findViewById(R.id.et_name);


        setUpTopProfile();
        mNavigationView = findViewById(R.id.settings_navigation);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

        @Override
         public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
             switch (menuItem.getItemId()) {
                 case R.id.shareFacebook:
                     Intent shareOnFB = new Intent(ProfileActivity.this, ShareEventActivity.class);
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

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //Change bottom navigation profile icon to filled
        bottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_filled_24);
        setUpBottomNav();
    }

    //set up for top of profile page
    private void setUpTopProfile() {
        //TODO change boolean to isOrg boolean user attribute to display correct profile
        if(false) {
            mLayoutForOrg.setVisibility(View.INVISIBLE);
            mLayoutForConsumer.setVisibility(View.VISIBLE);
        } else {
            mLayoutForOrg.setVisibility(View.VISIBLE);
            mLayoutForConsumer.setVisibility(View.INVISIBLE);
        }
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