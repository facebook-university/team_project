package com.example.dine_and_donate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        mDatabase = FirebaseDatabase.getInstance();

        //set up for top of profile page
        mUserName = findViewById(R.id.et_name);
        //TODO change boolean to isOrg boolean user attribute to display correct profile
        if(false) {
            //mViewFlipper.setDisplayedChild(mViewFlipper.indexOfChild(findViewById(R.id.forOrg)));
        } else {
            //mViewFlipper.setDisplayedChild(mViewFlipper.indexOfChild(findViewById(R.id.forConsumer)));
        }


        mTabLayout = findViewById(R.id.tabs_profile);
        mVoucherView = (ViewPager) findViewById(R.id.viewpager_id);
        mVoucherPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

       //Add Fragment Here

        mVoucherView.setAdapter(mVoucherPagerAdapter);
        mTabLayout.setupWithViewPager(mVoucherView);


        mVoucherView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //TODO indentation
        mNavigationView = (NavigationView) findViewById(R.id.settings_navigation);
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