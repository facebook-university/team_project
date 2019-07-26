package com.example.dine_and_donate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.dine_and_donate.HomeFragments.ListFragment;
import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.HomeFragments.NotificationsFragment;
import com.example.dine_and_donate.HomeFragments.ProfileFragment;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.parceler.Parcels;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout mDrawerNav;
    private ImageButton mBtnSwap;
    private boolean mShowButton = false;
    private boolean mIsOnMapView;
    private NotificationsFragment mNotificationsFragment = new NotificationsFragment();
    private MapFragment mMapFragment = new MapFragment();
    private ProfileFragment mProfileFragment = new ProfileFragment();
    private ListFragment mListFragment = new ListFragment();
    public User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDrawerNav = findViewById(R.id.drawerNav);
        mCurrentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        mBtnSwap = findViewById(R.id.btnSwap);
        mBtnSwap.setVisibility(View.INVISIBLE);
        mIsOnMapView = true;

        mBtnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExploreTab();
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, mProfileFragment)
                .addToBackStack(null) // TODO: look into if this can cause mem problem
                .commit();

        createDrawerNav();
        createBottomNav();
    }

    private void createBottomNav() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_filled_24);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.action_notify:
                        lockDrawer();
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_filled_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_outline_24);
                        fragment = mNotificationsFragment;
                        mShowButton = false;
                        break;
                    case R.id.action_map:
                        lockDrawer();
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_filled_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_outline_24);
                        fragment = mIsOnMapView ? mMapFragment : mListFragment;
                        mShowButton = true;
                        break;
                    case R.id.action_profile:
                        mDrawerNav.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_filled_24);
                        fragment = mProfileFragment;
                        mShowButton = false;
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null) // TODO: look into if this can cause mem problem
                        .commit();
                if(mShowButton) {
                    mBtnSwap.setVisibility(View.VISIBLE);
                } else {
                    mBtnSwap.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });
    }

    private void createDrawerNav() {
        Button mLogOutBtn = findViewById(R.id.logout);
        NavigationView mNavigationView = findViewById(R.id.settings_navigation);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.shareFacebook:
                        navigationHelper(ShareEventActivity.class);
                        return true;
                    case R.id.editProfile:
                        navigationHelper(EditProfileActivity.class);
                        return true;
                    case R.id.logout:
                        // TO DO
                        return true;
                }
                return true;
            }
        });
    }

    private void lockDrawer() {
        mDrawerNav.closeDrawers();
        mDrawerNav.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void navigationHelper(Class navigateToClass) {
        Intent intent = new Intent(HomeActivity.this, navigateToClass);
        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCurrentUser));
        startActivity(intent);
    }

    private void setExploreTab() {
        if(mShowButton) {
            if(mIsOnMapView) {
                mListFragment.setNearbyEvents(mMapFragment.getNearbyEvents());
                mListFragment.setRestaurantsJSON(mMapFragment.getRestaurantsNearbyJSON());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, mListFragment)
                        .addToBackStack(null)
                        .commit();
                mIsOnMapView = false;
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, mMapFragment)
                        .addToBackStack(null)
                        .commit();
                mIsOnMapView = true;
            }
        }
    }
}