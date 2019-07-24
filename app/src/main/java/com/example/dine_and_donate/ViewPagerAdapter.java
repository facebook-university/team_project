package com.example.dine_and_donate;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CurrentVouchersFragment();
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
}