package com.example.dine_and_donate;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdadpter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentOne = new ArrayList<>();
    private final List<String> fragmentOneTitles = new ArrayList<>();

    public ViewPagerAdadpter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int position) {
        return fragmentOne.get(position);
    }

    public int getCount() {
        return fragmentOneTitles.size();
    }

    public CharSequence getDescription(int position) {
        return fragmentOneTitles.get(position);
    }

    public void AddFragment(Fragment fragment, String description) {
        fragmentOne.add(fragment);
        fragmentOneTitles.add(description);
    }
}
