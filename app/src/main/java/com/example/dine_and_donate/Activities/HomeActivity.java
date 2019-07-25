package com.example.dine_and_donate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.dine_and_donate.EditProfileActivity;
import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.HomeFragments.NotificationsFragment;
import com.example.dine_and_donate.HomeFragments.ProfileFragment;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.ShareEventActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.parceler.Parcels;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout mDrawerNav;
    private Fragment mNotificationsFragment = new NotificationsFragment();
    private Fragment mMapFragment = new MapFragment();
    private Fragment mProfileFragment = new ProfileFragment();
    public User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDrawerNav = findViewById(R.id.drawerNav);
        mCurrentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

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
                        break;
                    case R.id.action_map:
                        lockDrawer();
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_filled_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_outline_24);
                        fragment = mMapFragment;
                        break;
                    case R.id.action_profile:
                        mDrawerNav.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_filled_24);
                        fragment = mProfileFragment;
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null) // TODO: look into if this can cause mem problem
                        .commit();
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
}