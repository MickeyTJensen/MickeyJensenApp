package com.example.myapp.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.myapp.AppNotificationManager;

public class TimerBroadcastReceiver extends BroadcastReceiver {
    private static int lastNotificationId = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("TURN_OFF_ALARM".equals(intent.getAction())) {
            // Logic to stop the alarm
            stopAlarmSound(context);
        } else {
            // Existing logic...
        }
    }
    public static int generateUniqueNotificationId() {
        // Increment the last generated notification ID and return it.
        return ++lastNotificationId;
    }
    private void stopAlarmSound(Context context) {
        Intent stopAlarmIntent = new Intent("com.example.myapp.STOP_ALARM");
        context.sendBroadcast(stopAlarmIntent);
    }
}
