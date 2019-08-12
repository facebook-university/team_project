package com.example.dine_and_donate.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.dine_and_donate.HomeFragments.ListFragment;
import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.HomeFragments.NotificationsFragment;
import com.example.dine_and_donate.HomeFragments.ProfileFragment;
import com.example.dine_and_donate.Notifications.MyReceiver;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.SearchDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

import java.util.Calendar;
import java.util.HashMap;

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
    private HashMap<String, User> mIdToOrg;

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
        boolean bool = currentUser.isOrg;
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
        MenuItem notifications = mBottomNavigationView.getMenu().findItem(R.id.action_notify);
        if (currentUser.isOrg) {
            notifications.setVisible(false);
            MenuItem profile = mBottomNavigationView.getMenu().findItem(R.id.action_profile);
            profile.setTitle("My Events");
            profile.setIcon(R.drawable.icons8_event_50);
        }
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
                        if (currentUser.isOrg) {
                            mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.icons8_event_50);
                        } else {
                            mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.like);
                        }
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_filled_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
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
                        if (currentUser.isOrg) {
                            mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.icons8_event_50_filled);
                        } else {
                            mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.like_filled);
                        }
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
        if ((mIsOnMapView || (query != null)) && (!mIsOnNotifications)) {
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
            if (mIsOnNotifications) {
                mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_filled_50);
                if (currentUser.isOrg) {
                    mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.icons8_event_50);
                } else {
                    mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.like);
                }
                MenuItem map = mBottomNavigationView.getMenu().findItem(R.id.action_map);
                map.setChecked(true);
                mBtnSwap.setVisible(true);
                mSearch.setVisible(!currentUser.isOrg);
                mLogOut.setVisible(false);
                mEditProfile.setVisible(false);
                mIsOnNotifications = false;
            }
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

    private void setUpNotificationWorker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // if time already happened, adds one day to trigger notification
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent triggerNotification = new Intent(HomeActivity.this, MyReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, triggerNotification, PendingIntent.FLAG_NO_CREATE);
        if (mPendingIntent == null) {
            AlarmManager alarmManageram = (AlarmManager) getSystemService(ALARM_SERVICE);
            mPendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, triggerNotification, PendingIntent.FLAG_CANCEL_CURRENT);
            // start it only it wasn't running already
            alarmManageram.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_HOUR, mPendingIntent);
        }
    }

    public void setMarkerLatLngToNull() {
        markerLatLng = null;
    }

    public LatLng getMarkerLatLng() {
        return markerLatLng;
    }

    public void setIdToOrg(HashMap<String, User> idToOrg) {
        this.mIdToOrg = idToOrg;
    }

    public HashMap<String, User> getIdToOrg() {
        return mIdToOrg;
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