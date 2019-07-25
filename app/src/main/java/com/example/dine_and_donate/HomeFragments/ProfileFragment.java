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

import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.EditProfileActivity;
import com.example.dine_and_donate.LoginActivity;
import com.example.dine_and_donate.MapActivity;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.NotificationsActivity;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.ShareEventActivity;
import com.example.dine_and_donate.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

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

    private ConstraintLayout mLayoutForOrg;
    private ConstraintLayout mLayoutForConsumer;

    private User currentUserModel;

    private HomeActivity HomeActivity;


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

        HomeActivity = (HomeActivity) getActivity();
        currentUserModel = HomeActivity.mCurrentUser;

        mLayoutForConsumer = view.findViewById(R.id.forConsumer);
        mLayoutForOrg = view.findViewById(R.id.forOrg);
        mTabLayout = view.findViewById(R.id.tabs_profile);
        mTabLayout.setupWithViewPager(mVoucherView);
        mOrgName = view.findViewById(R.id.org_name);
        mConsumerName = view.findViewById(R.id.cons_name);

        setUpTopProfile(currentUserModel.name);
    }

    //set up for top of profile page based on user type
    private void setUpTopProfile(String name) {
        //display orgView when user type is an organization
        if(currentUserModel.isOrg) {
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