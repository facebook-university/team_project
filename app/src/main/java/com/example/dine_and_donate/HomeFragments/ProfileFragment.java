package com.example.dine_and_donate.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Adapters.ViewPagerAdapter;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.google.android.material.tabs.TabLayout;

public class ProfileFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPagerAdapter mVoucherPagerAdapter;
    private ViewPager mVoucherView;
    private TextView mUserName;
    private TextView mBio;
    private ImageView mProfPic;
    private ImageView mBlurredPic;
    private Button mLogOutBtn;
    private ConstraintLayout mLayoutForOrg;
    private ConstraintLayout mLayoutForConsumer;
    private User mCurrentUserModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

        HomeActivity homeActivity = (HomeActivity) getActivity();
        mCurrentUserModel = homeActivity.currentUser;

        mLayoutForConsumer = view.findViewById(R.id.forConsumer);
        mLayoutForOrg = view.findViewById(R.id.forOrg);
        mTabLayout = view.findViewById(R.id.tabs_profile);
        mTabLayout.setupWithViewPager(mVoucherView);
        setUpTopProfile(mCurrentUserModel.name);
        if (mCurrentUserModel.isOrg) {
            mProfPic = view.findViewById(R.id.org_prof_pic);
            mUserName = view.findViewById(R.id.org_name);
        } else {
            mProfPic = view.findViewById(R.id.cons_prof_pic);
            mUserName = view.findViewById(R.id.cons_name);
        }

        mUserName.setText(mCurrentUserModel.name);
    }

    //set up for top of profile page based on user type
    private void setUpTopProfile(String name) {
        //display orgView when user type is an organization
        if (mCurrentUserModel.isOrg) {
            mLayoutForOrg.setVisibility(View.VISIBLE);
            mLayoutForConsumer.setVisibility(View.GONE);
        } else {
            mLayoutForOrg.setVisibility(View.GONE);
            mLayoutForConsumer.setVisibility(View.VISIBLE);
        }
    }
}