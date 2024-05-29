package com.example.myapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.myapp.timer.TimerStopWatchActivity;

public class AppNotificationManager {

    private static int notificationIdCounter = 0;
    public static final String CHANNEL_ID = "MickeyJensenAppChannel";
    private static final String CHANNEL_NAME = "Mickey Jensen's App";
    private static final String CHANNEL_DESCRIPTION = "Your channel description";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);

            // Define the action to turn off the alarm
            Intent turnOffIntent = new Intent(context, TimerStopWatchActivity.class);
            turnOffIntent.setAction("TURN_OFF_ALARM");
            PendingIntent turnOffPendingIntent = PendingIntent.getActivity(context, 0, turnOffIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create an action for turning off the alarm
            NotificationCompat.Action turnOffAction = new NotificationCompat.Action.Builder(
                    android.R.drawable.ic_lock_power_off, // Use the default Android power off icon
                    "Turn Off Alarm",
                    turnOffPendingIntent
            ).build();

            // Add the action to the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Title")
                    .setContentText("Content")
                    .addAction(turnOffAction) // Add the action to the notification
                    .setSmallIcon(R.drawable.ic_notification); // Replace with your icon resource

            // Define the notificationId
            int notificationId = generateUniqueNotificationId(); // You can use any unique integer as the notificationId

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Handle permission request here (e.g., show a message to the user or request permission).
                return;
            }
            notificationManager.notify(notificationId, builder.build());
        }
    }

    public static void sendNotification(Context context, String title, String content, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notification) // Replace with your icon resource
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Handle permission request here (e.g., show a message to the user or request permission).
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

    public static int generateUniqueNotificationId() {
        // Increment the counter to generate a new unique ID
        return notificationIdCounter++;
    }
}