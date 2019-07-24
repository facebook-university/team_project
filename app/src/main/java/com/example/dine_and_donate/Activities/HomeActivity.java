package com.example.dine_and_donate.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.HomeFragments.NotificationsFragment;
import com.example.dine_and_donate.HomeFragments.ProfileFragment;
import com.example.dine_and_donate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment notifications = new NotificationsFragment();
        final Fragment map = new MapFragment();
        final Fragment profile = new ProfileFragment();

        fragmentManager.beginTransaction().replace(R.id.flContainer, new ProfileFragment()).commit();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = profile;
                switch (item.getItemId()) {
                    case R.id.action_notify:
                        fragment = notifications;
                        break;
                    case R.id.action_map:
                        fragment = map;
                        break;
                    case R.id.action_profile:
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
}