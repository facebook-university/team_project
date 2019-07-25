package com.example.dine_and_donate.HomeFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.dine_and_donate.EditProfileActivity;
import com.example.dine_and_donate.LoginActivity;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.ShareEventActivity;
import com.example.dine_and_donate.ViewPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    //elements in layout
    private TabLayout mTabLayout;
    private ViewPagerAdapter mVoucherPagerAdapter;
    private ViewPager mVoucherView;
    private TextView mOrgName;
    private TextView mConsumerName;
    private TextView mBio;
    private ImageView mProfPic;
    private ImageView mBlurredPic;
    private Button mLogOutBtn;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle aToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private ConstraintLayout mLayoutForOrg;
    private ConstraintLayout mLayoutForConsumer;

    private User currentUserModel;

    private FirebaseDatabase mDatabase;
    private FirebaseUser mFbUser;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private boolean mIsOrg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mVoucherView = view.findViewById(R.id.viewpager_id);
        mVoucherPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mVoucherView.setAdapter(mVoucherPagerAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance();

//        mVoucherView = findViewById(R.id.viewpager_id);
//        mVoucherPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//        mVoucherView.setAdapter(mVoucherPagerAdapter);

        mLayoutForConsumer = view.findViewById(R.id.forConsumer);
        mLayoutForOrg = view.findViewById(R.id.forOrg);
        mTabLayout = view.findViewById(R.id.tabs_profile);
        mTabLayout.setupWithViewPager(mVoucherView);
        mNavigationView = view.findViewById(R.id.settings_navigation);
        mLogOutBtn = view.findViewById(R.id.logout);
        mOrgName = view.findViewById(R.id.org_name);
        mConsumerName = view.findViewById(R.id.cons_name);

        mTabLayout.setupWithViewPager(mVoucherView);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mFbUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = mDatabase.getReference().child("users").child(mFbUser.getUid());

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.shareFacebook:
                        Intent shareOnFB = new Intent(getActivity(), ShareEventActivity.class);
                        startActivity(shareOnFB);
                        return true;
                    case R.id.editProfile:
                        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.logout:
                        Intent logoutIntent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(logoutIntent);
                        return true;
                }
                return true;
            }
        });

        //retrieve values from database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mIsOrg = (Boolean) dataSnapshot.child("isOrg").getValue();
                setUpTopProfile(dataSnapshot.child("name").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //set up for top of profile page based on user type
    private void setUpTopProfile(String name) {
        //display orgView when user type is an organization
        if(mIsOrg) {
            mLayoutForOrg.setVisibility(View.VISIBLE);
            mLayoutForConsumer.setVisibility(View.GONE);
            mOrgName.setText(name);
        } else {
            mLayoutForOrg.setVisibility(View.GONE);
            mLayoutForConsumer.setVisibility(View.VISIBLE);
            mConsumerName.setText(name);
        }
    }
}