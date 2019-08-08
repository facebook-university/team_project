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
    private Fragment mDefaultFragment;
    private DialogFragment mDialogFragment;
    public User currentUser;
    private ProgressBar mProgressSpinner;
    private Button mBtnSwap;
    private boolean mShowButton = false;
    private boolean mIsOnMapView;
    private boolean mIsOnNotifications;
    private PendingIntent mPendingIntent;
    public LatLng markerLatLng;
    private String mStack = "map";
    private String mClickedOnID;
    private Boolean mIsOnProfileView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        currentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.top_bar);

        mDefaultFragment = (getIntent().getStringExtra("defaultFragment") != null) ? mMapFragment : mProfileFragment;
        String latitude = getIntent().getStringExtra("latitude");
        String longitude = getIntent().getStringExtra("longitude");
        markerLatLng = (latitude != null && longitude != null) ? new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)) : null;

        mProgressSpinner = findViewById(R.id.progressSpinner);
        setLoading(false);
        mBtnSwap = findViewById(R.id.btnSwap);
        mBtnSwap.setVisibility(View.INVISIBLE);
        mBtnSwap.setText(R.string.swap_list);
        mIsOnMapView = true;
        mIsOnProfileView = true;

        mBtnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExploreTab(null);
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, mDefaultFragment)
                .addToBackStack(mStack)
                .commit();

        createBottomNav();

        if (!currentUser.isOrg) {
            //createConsumerBottomNav();
            setUpNotificationWorker();
        } else {

        }
    }

    private void createBottomNav() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        Integer iconFilledDefault = (mDefaultFragment.equals(mMapFragment)) ? R.drawable.icons8_map_filled_50
                : R.drawable.instagram_user_filled_24;
        if(currentUser.isOrg) {
            mBottomNavigationView.getMenu().findItem(R.id.action_notify).setVisible(false);
        } else {
            mBottomNavigationView.getMenu().findItem(R.id.action_notify).setVisible(true);

        }

        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(iconFilledDefault);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.action_notify:
                        mIsOnProfileView = false;
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_filled_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_outline_24);
                        fragment = mNotificationsFragment;
                        mIsOnNotifications = true;
                        mShowButton = false;
                        mStack = "notify";
                        break;
                    case R.id.action_map:
                        mIsOnProfileView = false;
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_filled_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_outline_24);
                        fragment = mIsOnMapView ? mMapFragment : mListFragment;
                        mIsOnNotifications = false;
                        mStack = mIsOnMapView ? "map" : "list";
                        mShowButton = true;
                        break;
                    case R.id.action_profile:
                        if(!currentUser.isOrg) {
                            mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        }
                        mIsOnProfileView = true;
                        mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_50);
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_filled_24);
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
                finish();
                break;

            case R.id.searchEvents:
                mDialogFragment = SearchDialogFragment.newInstance(mMapFragment.getOrgNames());
                mDialogFragment.show(getSupportFragmentManager(), "dialog");
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
                mBtnSwap.setText(R.string.swap_map);
            } else {
                getSupportFragmentManager().popBackStack("map", 0);
                mIsOnMapView = true;
                mBtnSwap.setText(R.string.swap_list);
            }
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContainer, mMapFragment)
                    .addToBackStack(null)
                    .commit();
            mIsOnMapView = true;
            mIsOnNotifications = false;
            mBtnSwap.setText(R.string.swap_list);
            mBottomNavigationView.getMenu().findItem(R.id.action_notify).setIcon(R.drawable.icons8_notification_50);
            mBottomNavigationView.getMenu().findItem(R.id.action_map).setIcon(R.drawable.icons8_map_filled_50);
            mBottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_outline_24);
        }
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

    private void setUpNotificationWorker() {

//        mNotificationsFragment = new NotificationsFragment();
//        mNotificationsFragment.setIdToOrg(mNotificationsFragment.getIdToOrg());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 17);
        calendar.set(Calendar.SECOND, 00);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent triggerNotification = new Intent(HomeActivity.this, MyReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, triggerNotification, 0);
        AlarmManager alarmManageram = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManageram.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, mPendingIntent);
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
        if(isLoading) {
            mProgressSpinner.setVisibility(View.VISIBLE);
        } else {
            mProgressSpinner.setVisibility(View.GONE);
        }
    }
}
