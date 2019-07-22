package com.example.dine_and_donate;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.dine_and_donate.Models.StaggeredRecyclerViewAdapter;
import com.example.dine_and_donate.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.parceler.Parcels;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    MenuItem createEvent;

    private static final String TAG = "ProfileActivity";
    private static final int NUM_COLUMNS = 2;

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mDescription = new ArrayList<>();

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        initImageBitmaps();
        initRecyclerView();

        mCurrentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Change bottom navigation profile icon to filled
        bottomNavigationView.getMenu().findItem(R.id.action_profile).setIcon(R.drawable.instagram_user_filled_24);

        //Add click listener to bottom navigation bar for navigating between views
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_notify:
                        //Go to Notifications
                        navigationHelper(NotificationsActivity.class);
                        break;
                    case R.id.action_map:
                        //Go to Map
                        navigationHelper(MapActivity.class);
                        break;
                    case R.id.action_profile:
                        break;
                    default: break;
                }
                return true;
            }
        });
    }

    private void initImageBitmaps() {
        for(int i = 0; i < 20; i++) {
            mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        }
        for(int i = 0; i < 10; i++) {
            mNames.add("Tree");
            mNames.add("fjcutcnerdfdluvhbuegnecgvlkclbidjvnlvfubjrbeugtfdrtnikledvtbhguvuhrtjcvcfguekrfrihjfehbjllfdutbg");
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_vouchers);
        StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter =
                new StaggeredRecyclerViewAdapter(this, mNames, mImageUrls);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
    }

    private void navigationHelper(Class activity) {
        final Intent intent = new Intent(this, activity);
        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCurrentUser));
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        createEvent = menu.findItem(R.id.createEvent);
        createEvent.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                createEvent();
                return false;
            }
        });

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Associate searchable configuration with the SearchView
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void createEvent() {
        System.out.println("Clicked on Create Event!");
        //TO DO
    }
}