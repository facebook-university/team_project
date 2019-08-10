package com.example.dine_and_donate.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WorkManager workManager;
        workManager = WorkManager.getInstance(context);
        System.out.println("CALLED");
        // Enqueue our work to manager
        workManager.enqueue(OneTimeWorkRequest.from(NotifyWorker.class));
    }
}
