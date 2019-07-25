package com.example.dine_and_donate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.dine_and_donate.EditProfileActivity;
import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.HomeFragments.NotificationsFragment;
import com.example.dine_and_donate.HomeFragments.ProfileFragment;
import com.example.dine_and_donate.LoginActivity;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.ShareEventActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.parceler.Parcels;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private DrawerLayout drawerNav;
    private Fragment notifications = new NotificationsFragment();
    private Fragment map = new MapFragment();
    private Fragment profile = new ProfileFragment();
    public User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentManager = getSupportFragmentManager();
        drawerNav = findViewById(R.id.drawerNav);
        mCurrentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        fragmentManager.beginTransaction()
                .replace(R.id.flContainer, profile)
                .addToBackStack(null) // TODO: look into if this can cause mem problem
                .commit();

        createDrawerNav();
        createBottomNav();
    }

    private void createBottomNav() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_filled_24);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.action_notify:
                        lockDrawer();
                        bottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_filled_50);
                        bottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_50);
                        bottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_outline_24);
                        fragment = notifications;
                        break;
                    case R.id.action_map:
                        lockDrawer();
                        bottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        bottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_filled_50);
                        bottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_outline_24);
                        fragment = map;
                        break;
                    case R.id.action_profile:
                        drawerNav.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        bottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        bottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_50);
                        bottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_filled_24);
                        fragment = profile;
                        break;
                }
                fragmentManager.beginTransaction()
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
                        Intent shareOnFB = new Intent(HomeActivity.this, ShareEventActivity.class);
                        startActivity(shareOnFB);
                        return true;
                    case R.id.editProfile:
                        Intent intent = new Intent(HomeActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.logout:
                        Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(logoutIntent);
                        return true;
                }
                return true;
            }
        });
    }

    private void lockDrawer() {
        drawerNav.closeDrawers();
        drawerNav.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
}