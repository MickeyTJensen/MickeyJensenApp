package com.example.myapp.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

import com.example.myapp.AppNotificationManager;
import com.example.myapp.R;
// Add any other imports you need

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderText = intent.getStringExtra("reminder_text");
        int notificationId = (int) System.currentTimeMillis(); // Example of generating a unique ID
        AppNotificationManager.sendNotification(context, "Reminder", reminderText, notificationId);
    }
}

