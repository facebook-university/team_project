package com.example.dine_and_donate.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dine_and_donate.CurrentVouchersFragment;
import com.example.dine_and_donate.OldVouchersFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UpcomingVouchersFragment();
            case 1:
                return new OldVouchersFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Current Vouchers";
            case 1:
                return "Past Vouchers";
            default:
                return null;
        }
    }
}
