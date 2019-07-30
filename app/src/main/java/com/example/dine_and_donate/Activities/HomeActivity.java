package com.example.dine_and_donate.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.dine_and_donate.EditProfileActivity;
import com.example.dine_and_donate.HomeFragments.ListFragment;
import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.HomeFragments.NotificationsFragment;
import com.example.dine_and_donate.HomeFragments.ProfileFragment;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.NotifyWorker;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.ShareEventActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import org.parceler.Parcels;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout mDrawerNav;

    private NotificationsFragment mNotificationsFragment = new NotificationsFragment();
    private MapFragment mMapFragment = new MapFragment();
    private ProfileFragment mProfileFragment = new ProfileFragment();
    private ListFragment mListFragment = new ListFragment();

    private ImageButton mBtnSwap;
    private boolean mShowButton = false;
    private boolean mIsOnMapView;
    public User mCurrentUser;
    private PendingIntent mPendingIntent;
    public LatLng markerLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDrawerNav = findViewById(R.id.drawerNav);
        mCurrentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        Fragment mDefaultFragment = (getIntent().getStringExtra("defaultFragment") != null) ? mMapFragment : mProfileFragment;
        String latitude = getIntent().getStringExtra("latitude");
        String longitude = getIntent().getStringExtra("longitude");
        markerLatLng = (latitude != null && longitude != null) ? new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)) : null;

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
                .replace(R.id.flContainer, mDefaultFragment)
                .addToBackStack(null) // TODO: look into if this can cause mem problem
                .commit();

        createDrawerNav();
        createBottomNav();
        if (!mCurrentUser.isOrg) {
            setUpNotificationWorker();
        }
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
        if (mShowButton) {
            if (mIsOnMapView) {
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

    private void setUpNotificationWorker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent myIntent = new Intent(HomeActivity.this, MyReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, myIntent, 0);
        AlarmManager alarmManageram = (AlarmManager)getSystemService(ALARM_SERVICE);

        alarmManageram.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, mPendingIntent);
    }

    public static class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            WorkManager workManager;
            workManager = WorkManager.getInstance(context);
            // Enqueue our work to manager
            workManager.enqueue(OneTimeWorkRequest.from(NotifyWorker.class));
        }
    }
}