package com.example.dine_and_donate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.Listeners.OnSwipeTouchListener;

public class CustomViewPager extends ViewPager {
    private static final int SWIPE_THRESHOLD = 50;
    private float x1, x2, y1, y2;
    private Boolean result = true;

    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                y1 = event.getRawY();
                x1 = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                y2 = event.getRawY();
                x2 = event.getRawX();

                float diffY = y2 - y1;
                float diffX = x2 - x1;
                x1 = 0; x2 = 0; y1 = 0; y2 = 0;

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    result = true;
                    return super.onTouchEvent(event);
                } else if (diffY > SWIPE_THRESHOLD) {
                    result = false;
                    return onTouchEvent(event);
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (result) {
            return super.onTouchEvent(event);
        } else {
            result = true;
            View slideView = getRootView().findViewById(R.id.slide_menu);
            MapFragment mapFragment = new MapFragment();
            mapFragment.slideDownMenu(slideView);
            return false;
        }
    }
}
