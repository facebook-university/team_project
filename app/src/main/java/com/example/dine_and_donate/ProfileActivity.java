package com.example.dine_and_donate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileActivity extends AppCompatActivity {

    //elements in layout
    private TabLayout mTabLayout;
    private ViewPagerAdapter mVoucherPagerAdapter;
    private ViewPager mVoucherView;
    private TextView mOrgName;
    private TextView mConsumerName;
    private TextView mBio;
    private ImageView mProfPic;
    private ImageView mBlurredPic;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle aToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private ConstraintLayout mLayoutForOrg;
    private ConstraintLayout mLayoutForConsumer;

    private User currentUserModel;
    private BottomNavigationView bottomNavigationView;

    private FirebaseDatabase mDatabase;
    private FirebaseUser mFbUser;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private boolean mIsOrg;

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
        mNavigationView = findViewById(R.id.settings_navigation);

        mOrgName = findViewById(R.id.org_name);
        mConsumerName = findViewById(R.id.cons_name);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mFbUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = mDatabase.getReference().child("users").child(mFbUser.getUid());

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

        //retrieve values from database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mIsOrg = (Boolean) dataSnapshot.child("isOrg").getValue();
                setUpTopProfile(dataSnapshot.child("name").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //set up for top of profile page based on user type
    private void setUpTopProfile(String name) {
       //display orgView when user type is an organization
        if(mIsOrg) {
            mLayoutForOrg.setVisibility(View.VISIBLE);
            mLayoutForConsumer.setVisibility(View.INVISIBLE);
            Log.d("name", name);
            mOrgName.setText(name);
        } else {
            mLayoutForOrg.setVisibility(View.INVISIBLE);
            mLayoutForConsumer.setVisibility(View.VISIBLE);
            Log.d("consumer name", name);
            mConsumerName.setText(name);
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