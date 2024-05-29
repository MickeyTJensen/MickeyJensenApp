package com.example.myapp.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapp.R;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReminderActivity extends AppCompatActivity {

    private NumberPicker hourPicker, minutePicker;
    private CheckBox[] dayCheckboxes = new CheckBox[7]; // Assuming checkbox IDs are checkbox_mon, checkbox_tue, etc.
    private Button addButton;
    private ListView remindersListView;
    private List<Reminder> reminders;
    private ReminderAdapter adapter;
    private EditText editTextReminderName;
    private int reminderIdCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        addButton = findViewById(R.id.addButton);
        remindersListView = findViewById(R.id.remindersListView);
        editTextReminderName = findViewById(R.id.editTextReminderName);

        // Initialize hour and minute pickers
        hourPicker.setMaxValue(23);
        hourPicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setMinValue(0);

        // Initialize checkboxes (example for Monday)
        dayCheckboxes[0] = findViewById(R.id.checkbox_mon);
        dayCheckboxes[1] = findViewById(R.id.checkbox_tue);
        dayCheckboxes[2] = findViewById(R.id.checkbox_wed);
        dayCheckboxes[3] = findViewById(R.id.checkbox_thu);
        dayCheckboxes[4] = findViewById(R.id.checkbox_fri);
        dayCheckboxes[5] = findViewById(R.id.checkbox_sat);
        dayCheckboxes[6] = findViewById(R.id.checkbox_sun);

        // Initialize the reminders list and adapter
        reminders = new ArrayList<>();
        loadReminders();
        adapter = new ReminderAdapter(this, reminders);
        remindersListView.setAdapter(adapter);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminder();
            }
        });
        Button resetReminderButton = findViewById(R.id.resetReminderButton);
        resetReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetReminders();
            }
        });
    }

    private void addReminder() {
        String reminderName = editTextReminderName.getText().toString().trim();
        if (reminderName.isEmpty()) {
            Toast.makeText(this, "Please enter a reminder name.", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = hourPicker.getValue();
        int minute = minutePicker.getValue();
        boolean[] days = new boolean[7];
        for (int i = 0; i < 7; i++) {
            days[i] = dayCheckboxes[i].isChecked();
        }

        Reminder newReminder = new Reminder(hour, minute, days, true, reminderName);
        reminders.add(newReminder);
        adapter.notifyDataSetChanged();
        editTextReminderName.setText("");

        saveReminders();

        // Get an instance of AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Check for permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            scheduleReminder(newReminder, generateUniqueReminderId());
        } else {
            // Redirect user to system settings
            Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }

    private void saveReminders() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(reminders);
        editor.putString("reminders", json);
        editor.apply();

        // Log the JSON string
        Log.d("ReminderActivity", "Saved reminders: " + json);
    }

    private void loadReminders() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("reminders", null);
        Type type = new TypeToken<ArrayList<Reminder>>() {}.getType();

        List<Reminder> loadedReminders = gson.fromJson(json, type);

        if (loadedReminders != null) {
            reminders.addAll(loadedReminders);
        }
    }
    private void scheduleReminder(Reminder reminder, int reminderId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
        intent.putExtra("reminder_text", reminder.getName()); // Assuming Reminder has a getText() method

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reminderId, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, reminder.getHour());
        calendar.set(Calendar.MINUTE, reminder.getMinute());
        calendar.set(Calendar.SECOND, 0);

        // If reminder time is in the past, set it for the next day
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
    private int generateUniqueReminderId() {
        return reminderIdCounter++;
    }
    private void resetReminders() {
        // Clear the list of reminders
        reminders.clear();

        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();

        // Clear reminders from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("reminders");
        editor.apply();
    }
}

