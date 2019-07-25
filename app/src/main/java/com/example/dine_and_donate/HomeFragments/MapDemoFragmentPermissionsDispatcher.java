package com.example.dine_and_donate.HomeFragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import permissions.dispatcher.PermissionUtils;

public final class MapDemoFragmentPermissionsDispatcher {

    private static final int REQUEST_GETMYLOCATION = 0;

    private static final String[] PERMISSION_GETMYLOCATION = new String[] {"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION"};

    private static final int REQUEST_STARTLOCATIONUPDATES = 1;

    private static final String[] PERMISSION_STARTLOCATIONUPDATES = new String[] {"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION"};

    private MapDemoFragmentPermissionsDispatcher() {
    }

    static void getMyLocationWithPermissionCheck(MapFragment fragment) {
        Context target = fragment.getView().getContext();
        if (PermissionUtils.hasSelfPermissions(target, PERMISSION_GETMYLOCATION)) {
            fragment.getMyLocation();
        } else {
            ActivityCompat.requestPermissions(fragment.getActivity(), PERMISSION_GETMYLOCATION, REQUEST_GETMYLOCATION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    static void startLocationUpdatesWithPermissionCheck(MapFragment fragment) {
        Context target = fragment.getView().getContext();
        if (PermissionUtils.hasSelfPermissions(target, PERMISSION_STARTLOCATIONUPDATES)) {
            fragment.startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(fragment.getActivity(), PERMISSION_STARTLOCATIONUPDATES, REQUEST_STARTLOCATIONUPDATES);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    static void onRequestPermissionsResult(MapFragment  target, int requestCode,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GETMYLOCATION:
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    target.getMyLocation();
                }
                break;
            case REQUEST_STARTLOCATIONUPDATES:
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    target.startLocationUpdates();
                }
                break;
            default:
                break;
        }
    }

}
