package com.example.myapp.timer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.Notification;
import com.example.myapp.AppNotificationManager;
import com.example.myapp.R;

public class TimerStopWatchActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private TextView timerText, stopwatchText;
    private Button startTimerButton, setTimerButton, resetTimerButton, startStopwatchButton, resetStopwatchButton;
    private CountDownTimer countDownTimer;
    private long initialTimeInMillis = 600000; // Default 10 minutes for example
    private long timeLeftInMillis = initialTimeInMillis;
    private boolean timerRunning = false;
    private NotificationCompat.Builder builder;
    private Handler stopwatchHandler = new Handler();
    private long stopwatchStartTime = 0L;
    private boolean stopwatchRunning = false;
    RemoteViews remoteViews;
    private MediaPlayer alarmMediaPlayer;

    private static final String CHANNEL_ID = "MickeyJensenAppChannel";
    private AppNotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 1;
    private BroadcastReceiver stopAlarmReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_stopwatch);

        // Initialize and register the BroadcastReceiver
        stopAlarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.example.myapp.STOP_ALARM".equals(intent.getAction())) {
                    stopAlarm();
                }
            }
        };
        IntentFilter filter = new IntentFilter("com.example.myapp.STOP_ALARM");
        registerReceiver(stopAlarmReceiver, filter);

        // Create the notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name); // Use the string resource
            String description = getString(R.string.channel_description); // Use the string resource
            int importance = NotificationManager.IMPORTANCE_HIGH; // Set the importance level
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }



        // Initialize UI components
        timerText = findViewById(R.id.timerText);
        stopwatchText = findViewById(R.id.stopwatchText);
        startTimerButton = findViewById(R.id.startTimerButton);
        setTimerButton = findViewById(R.id.setTimerButton);
        resetTimerButton = findViewById(R.id.resetTimerButton);
        startStopwatchButton = findViewById(R.id.startStopwatchButton);
        resetStopwatchButton = findViewById(R.id.resetStopwatchButton);

        // Initialize notification manager and RemoteViews for custom notifications
        notificationManager = new AppNotificationManager();
        remoteViews = new RemoteViews(getPackageName(), R.layout.custom_timer_notification);



        // Set click listeners for buttons
        setTimerButton.setOnClickListener(v -> showTimerSetDialog());
        startTimerButton.setOnClickListener(v -> toggleTimer());
        resetTimerButton.setOnClickListener(v -> {
            // Stop the alarm
            stopAlarm();

            // Reset the timer to its initial state
            resetTimer();
        });

        startStopwatchButton.setOnClickListener(v -> toggleStopwatch());
        resetStopwatchButton.setOnClickListener(v -> resetStopwatch());

        // Initialize the builder for notifications
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Timer")
                .setContentText("Countdown in progress...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCustomBigContentView(remoteViews);
    }

    private void showTimerSetDialog() {
        // Create a dialog to set the timer
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_set_timer, null);
        builder.setView(dialogView);

        // Initialize NumberPickers for hours and minutes
        final NumberPicker numberPickerHour = dialogView.findViewById(R.id.numberPickerHour);
        final NumberPicker numberPickerMinute = dialogView.findViewById(R.id.numberPickerMinute);
        numberPickerHour.setMaxValue(23);
        numberPickerHour.setMinValue(0);
        numberPickerMinute.setMaxValue(59);
        numberPickerMinute.setMinValue(0);

        // Set positive button to set the timer duration
        builder.setPositiveButton("Set", (dialog, which) -> {
            int hours = numberPickerHour.getValue();
            int minutes = numberPickerMinute.getValue();
            timeLeftInMillis = (hours * 3600 + minutes * 60) * 1000L;
            updateTimerText();
        });

        // Set negative button to cancel
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void toggleTimer() {
        if (!timerRunning) {
            startTimer();
        } else {
            pauseTimer();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            public void onFinish() {
                timerRunning = false;
                startTimerButton.setText("Start");

                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alarmSound == null) {
                    // Fallback to notification sound if the alarm sound is not available
                    alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                }


                // Start the alarm sound
                alarmMediaPlayer = MediaPlayer.create(TimerStopWatchActivity.this, alarmSound);
                alarmMediaPlayer.setLooping(true);
                alarmMediaPlayer.start();

                sendTimerNotification(); // Notify when timer finishes
            }
        }.start();
        timerRunning = true;
        startTimerButton.setText("Pause");
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        startTimerButton.setText("Start");
    }

    private void resetTimer() {
        timeLeftInMillis = initialTimeInMillis;
        updateTimerText();
        timerRunning = false;
        startTimerButton.setText("Start");
    }

    private void sendTimerNotification() {
        String notificationTitle = "Timer Finished";
        String notificationContent = "Your timer has finished.";

        // Create an Intent for the broadcast receiver to stop the alarm
        Intent turnOffIntent = new Intent("com.example.myapp.STOP_ALARM");
        PendingIntent turnOffPendingIntent = PendingIntent.getBroadcast(this, 0, turnOffIntent, 0);

        // Use NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(0, "Turn Off Alarm", turnOffPendingIntent) // Add action with text
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationContent)); // Explicitly set the style

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void updateTimerText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerText.setText(timeFormatted);
    }

    private void toggleStopwatch() {
        if (!stopwatchRunning) {
            startStopwatch();
        } else {
            pauseStopwatch();
        }
    }

    private void startStopwatch() {
        if (stopwatchStartTime == 0L) {
            stopwatchStartTime = SystemClock.elapsedRealtime();
        } else {
            stopwatchStartTime = SystemClock.elapsedRealtime() - (SystemClock.elapsedRealtime() - stopwatchStartTime);
        }
        stopwatchHandler.postDelayed(stopwatchRunnable, 0);
        stopwatchRunning = true;
        startStopwatchButton.setText("Stop");
    }

    private void pauseStopwatch() {
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
        stopwatchRunning = false;
        startStopwatchButton.setText("Start");
    }

    private final Runnable stopwatchRunnable = new Runnable() {
        public void run() {
            long elapsedMillis = SystemClock.elapsedRealtime() - stopwatchStartTime;
            int minutes = (int) (elapsedMillis / 60000);
            int seconds = (int) (elapsedMillis % 60000) / 1000;
            int milliseconds = (int) (elapsedMillis % 1000) / 10;
            stopwatchText.setText(String.format("%02d:%02d:%02d", minutes, seconds, milliseconds));
            stopwatchHandler.postDelayed(this, 10);
        }
    };

    private void resetStopwatch() {
        stopwatchHandler.removeCallbacks(stopwatchRunnable);
        stopwatchStartTime = 0L;
        stopwatchRunning = false;
        stopwatchText.setText("00:00:00");
        startStopwatchButton.setText("Start");
    }

    private void updateNotificationTimerText() {
        // Calculate the remaining time and format it
        String formattedTime = formatTime(timeLeftInMillis);

        // Update the TextView in the RemoteViews
        remoteViews.setTextViewText(R.id.timerTextView, formattedTime);

        // Update the notification using the updated RemoteViews
        updateNotification();
    }

    private void updateNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MickeyJensenApp")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Timer")
                .setContentText("Countdown in progress...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCustomBigContentView(remoteViews); // Set the updated RemoteViews

        // Add other configurations and actions to your notification builder

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, update the notification
                updateNotification();
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
    }

    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void stopAlarm() {
        if (alarmMediaPlayer != null && alarmMediaPlayer.isPlaying()) {
            alarmMediaPlayer.stop();
            alarmMediaPlayer.release();
            alarmMediaPlayer = null;
        }
        // Reset the timer or other UI updates...
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alarmMediaPlayer != null) {
            alarmMediaPlayer.release();
            alarmMediaPlayer = null;
        }
        unregisterReceiver(stopAlarmReceiver);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ("com.example.myapp.STOP_ALARM".equals(intent.getAction())) {
            stopAlarm();
        }
    }

private void handleIntent(Intent intent) {
    if (intent != null && "TURN_OFF_ALARM".equals(intent.getAction())) {
        // Stop the alarm and reset the timer
        stopAlarm();
        resetTimer();
    }
}

}
