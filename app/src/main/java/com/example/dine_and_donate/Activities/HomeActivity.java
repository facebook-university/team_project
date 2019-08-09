package com.example.dine_and_donate.Activities;

import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.dine_and_donate.HomeFragments.ListFragment;
import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.HomeFragments.NotificationsFragment;
import com.example.dine_and_donate.HomeFragments.ProfileFragment;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.NotifyWorker;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.SearchDialogFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
    private DialogFragment mDialogFragment;
    public User currentUser;
    private MenuItem mProgressSpinner;
    private MenuItem mBtnSwap;
    private MenuItem mLogOut;
    private MenuItem mSearch;
    private MenuItem mEditProfile;
    private boolean mShowButton = true;
    private boolean mIsOnMapView;
    private boolean mIsOnNotifications;
    private PendingIntent mPendingIntent;
    public LatLng markerLatLng;
    private String mStack = "map";
    private String mClickedOnID;
    private Boolean mIsOnProfileView;
    private boolean mNewSavedEvent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        currentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.top_bar);

        String latitude = getIntent().getStringExtra("latitude");
        String longitude = getIntent().getStringExtra("longitude");
        markerLatLng = (latitude != null && longitude != null) ? new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)) : null;

        mIsOnMapView = true;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, mMapFragment)
                .addToBackStack(mStack)
                .commit();

        createBottomNav();
        if (!currentUser.isOrg) {
            setUpNotificationWorker();
        }
    }

    private void createBottomNav() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        Integer iconFilledDefault = R.drawable.icons8_map_filled_50;
        MenuItem defaultMap = mBottomNavigationView.getMenu().findItem(R.id.action_map);
        defaultMap.setIcon(iconFilledDefault);
        defaultMap.setChecked(true);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.action_notify:
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_filled_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.like);
                        mBtnSwap.setVisible(false);
                        mSearch.setVisible(false);
                        mLogOut.setVisible(true);
                        mEditProfile.setVisible(true);
                        fragment = mNotificationsFragment;
                        mIsOnNotifications = true;
                        mShowButton = false;
                        mStack = "notify";
                        break;
                    case R.id.action_map:
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_filled_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.like);
                        mBtnSwap.setVisible(true);
                        mSearch.setVisible(!currentUser.isOrg);
                        mLogOut.setVisible(false);
                        mEditProfile.setVisible(false);
                        fragment = mIsOnMapView ? mMapFragment : mListFragment;
                        mIsOnNotifications = false;
                        mStack = mIsOnMapView ? "map" : "list";
                        mShowButton = true;
                        break;
                    case R.id.action_profile:
                        mBtnSwap.setVisible(false);
                        mSearch.setVisible(false);
                        mLogOut.setVisible(true);
                        mEditProfile.setVisible(true);
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.like_filled);
                        fragment = mProfileFragment;
                        mIsOnNotifications = false;
                        mShowButton = false;
                        mStack = "profile";
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(mStack)
                        .commit();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_settings_drawer, menu);
        mProgressSpinner = menu.findItem(R.id.miActionProgress);
        mBtnSwap = menu.findItem(R.id.btnSwap);
        mSearch = menu.findItem(R.id.searchEvents);
        mLogOut = menu.findItem(R.id.log_out);
        mEditProfile = menu.findItem(R.id.edit_profile);
        mBtnSwap.setTitle(R.string.swap_list);
        mBtnSwap.setIcon(R.drawable.list);
        if (mShowButton) {
            mBtnSwap.setVisible(true);
            mSearch.setVisible(!currentUser.isOrg);
            mLogOut.setVisible(false);
            mEditProfile.setVisible(false);
        }
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

            case R.id.searchEvents:
                mDialogFragment = SearchDialogFragment.newInstance(mMapFragment.getOrgNames());
                mDialogFragment.show(getSupportFragmentManager(), "dialog");
                break;

            case R.id.btnSwap:
                setExploreTab(null);
                break;
        }
        return true;
    }

    private void navigationHelper(Class navigateToClass) {
        Intent intent = new Intent(HomeActivity.this, navigateToClass);
        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(currentUser));
        startActivity(intent);
    }

    // todo: this function should be made cleaner
    public void setExploreTab(final String query) {

        if (mShowButton) {
            if (mIsOnMapView || (query != null)) {
                mListFragment = new ListFragment();
                mListFragment.setAllEvents(mMapFragment.getAllEvents());
                mListFragment.setRestaurantsJSON(mMapFragment.getRestaurantsNearbyJSON());
                mListFragment.setIdToRestaurant(mMapFragment.getIdToRestaurant());
                mListFragment.setIdToOrg(mMapFragment.getIdToOrg());
                mListFragment.setLocation(mMapFragment.getCurrentLocation());
                mListFragment.setQueryOrgId(mMapFragment.getNameToId().get(query));
                mListFragment.setOrgNames(mMapFragment.getOrgNames());
                mStack = "list";
                if (!getSupportFragmentManager().popBackStackImmediate("list", 0)) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.flContainer, mListFragment)
                            .addToBackStack(mStack)
                            .commit();
                }
                mIsOnMapView = false;
                mBtnSwap.setTitle(R.string.swap_map);
                mBtnSwap.setIcon(R.drawable.map);
            } else {
                getSupportFragmentManager().popBackStack("map", 0);
                mIsOnMapView = true;
                mBtnSwap.setTitle(R.string.swap_list);
                mBtnSwap.setIcon(R.drawable.list);
                // if an item on the list was clicked, generate markers and zoom to selected location
                if (mClickedOnID != null) {
                    if (!currentUser.isOrg) {
                        mMapFragment.generateMarkersEvents();
                    } else {
                        Location currentLocation = mMapFragment.getCurrentLocation();
                        mMapFragment.generateMarkersRestaurants(Double.toString(currentLocation.getLongitude()), Double.toString(currentLocation.getLatitude()));
                    }
                }
            }
        }
    }

    private void setUpNotificationWorker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
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

    public void setMarkerLatLng(LatLng markerLatLng) {
        this.markerLatLng = markerLatLng;
    }

    public String getClickedOnID() {
        return mClickedOnID;
    }

    public void setClickedOnID(String mClickedOnID) {
        this.mClickedOnID = mClickedOnID;
    }

    public void setClickedOnIdToNull() {
        mClickedOnID = null;
    }

    public boolean isNewSavedEvent() {
        return mNewSavedEvent;
    }

    public void setNewSavedEvent(boolean mNewSavedEvent) {
        this.mNewSavedEvent = mNewSavedEvent;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setLoading(boolean isLoading) {
        if (isLoading) {
            mProgressSpinner.setVisible(true);
        } else {
            mProgressSpinner.setVisible(false);
        }
    }
}