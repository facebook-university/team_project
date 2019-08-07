package com.example.dine_and_donate.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.dine_and_donate.HomeFragments.ListFragment;
import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.HomeFragments.NotificationsFragment;
import com.example.dine_and_donate.HomeFragments.ProfileFragment;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.NotifyWorker;
import com.example.dine_and_donate.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;

    private NotificationsFragment mNotificationsFragment = new NotificationsFragment();
    private MapFragment mMapFragment = new MapFragment();
    private ProfileFragment mProfileFragment = new ProfileFragment();
    private ListFragment mListFragment = new ListFragment();
    private Fragment mDefaultFragment;
    public User currentUser;
    private ImageButton mBtnSwap;
    private boolean mShowButton = false;
    private boolean mIsOnMapView;
    private PendingIntent mPendingIntent;
    public LatLng markerLatLng;
    private boolean mShowMenuOption;
    private MenuItem mItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        currentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        mDefaultFragment = (getIntent().getStringExtra("defaultFragment") != null) ? mMapFragment : mProfileFragment;
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

        createBottomNav();
        if (!currentUser.isOrg) {
            setUpNotificationWorker();
        }
    }

    private void createBottomNav() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        Integer iconFilledDefault = (mDefaultFragment.equals(mMapFragment)) ? R.drawable.icons8_map_filled_50
                : R.drawable.instagram_user_filled_24;
        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(iconFilledDefault);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.action_notify:
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_filled_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_outline_24);
                        fragment = mNotificationsFragment;
                        mShowButton = false;
                        break;
                    case R.id.action_map:
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_filled_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_outline_24);
                        fragment = mIsOnMapView ? mMapFragment : mListFragment;
                        mShowButton = true;
                        break;
                    case R.id.action_profile:
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
                if (mShowButton) {
                    mBtnSwap.setVisibility(View.VISIBLE);
                } else {
                    mBtnSwap.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        inflater.inflate(R.menu.profile_settings_drawer, menu);  // Use filter.xml from step 1
        getMenuInflater().inflate(R.menu.profile_settings_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.edit_profile:
                navigationHelper(EditProfileActivity.class);
                break;

            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                navigationHelper(LoginActivity.class);
                break;
        }
        return true;
    }

    private void navigationHelper(Class navigateToClass) {
        Intent intent = new Intent(HomeActivity.this, navigateToClass);
        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(currentUser));
        startActivity(intent);
    }

    private void setExploreTab() {
        if (mShowButton) {
            if (mIsOnMapView) {
                mListFragment = new ListFragment();
                mListFragment.setAllEvents(mMapFragment.getAllEvents());
                mListFragment.setRestaurantsJSON(mMapFragment.getRestaurantsNearbyJSON());
                mListFragment.setIdToRestaurant(mMapFragment.getIdToRestaurant());
                mListFragment.setIdToOrg(mMapFragment.getIdToOrg());
                mListFragment.setLocation(mMapFragment.getCurrentLocation());
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
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 26);
        calendar.set(Calendar.SECOND, 30);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent triggerNotification = new Intent(HomeActivity.this, MyReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, triggerNotification, 0);
        AlarmManager alarmManageram = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManageram.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, mPendingIntent);
    }

    public void setMarkerLatLngToNull() {
        markerLatLng = null;
    }

    public LatLng getMarkerLatLng() {
        return markerLatLng;
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
