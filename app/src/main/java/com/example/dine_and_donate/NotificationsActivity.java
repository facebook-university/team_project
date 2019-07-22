package com.example.dine_and_donate;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dine_and_donate.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.parceler.Parcels;

public class NotificationsActivity extends AppCompatActivity {

    private User mCurrentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_activity);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        mCurrentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        //Change bottom navigation notifications icon to filled
        bottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_filled_50);

        //Add click listener to bottom navigation bar for navigating between views
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_notify:
                        break;
                    case R.id.action_map:
                        //Go to Map
                        navigationHelper(MapActivity.class);
                        break;
                    case R.id.action_profile:
                        //Go to Profile
                        navigationHelper(ProfileActivity.class);
                        break;
                    default: break;
                }
                return true;
            }
        });
    }

    private void navigationHelper(Class activity) {
        final Intent intent = new Intent(this, activity);
        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCurrentUser));
        startActivity(intent);
    }
}
