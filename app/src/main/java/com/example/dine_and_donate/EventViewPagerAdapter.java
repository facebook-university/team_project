package com.example.dine_and_donate;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class EventViewPagerAdapter extends PagerAdapter {
    // This holds all the currently displayable views, in order from left to right.
    private ArrayList<View> views = new ArrayList<View>();
    public ArrayList<String> eventIds = new ArrayList<String>();

    @Override
    public int getItemPosition(Object object) {
        int index = views.indexOf(object);
        if (index == -1) {
            return POSITION_NONE;
        } else {
            return index;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = views.get(position);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get (position));
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int addView(View v, String id) {
        eventIds.add(id);
        return addView(v, views.size());
    }

    public int addView(View v, int position) {
        views.add(position, v);
        return position;
    }

    public String getEventId(int position) {
        return eventIds.get(position);
    }
}
