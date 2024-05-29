package com.example.myapp.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import com.example.myapp.R;
import java.util.List;

public class ReminderAdapter extends ArrayAdapter<Reminder> {

    public ReminderAdapter(Context context, List<Reminder> reminders) {
        super(context, 0, reminders);
    }

    // ViewHolder pattern for efficient view handling
    private static class ViewHolder {
        TextView reminderTime;
        TextView reminderText;
        Switch isActiveSwitch;
        Button deleteButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reminder_item, parent, false);
            holder = new ViewHolder();
            holder.reminderTime = convertView.findViewById(R.id.reminderTime);
            holder.reminderText = convertView.findViewById(R.id.reminderText);
            holder.isActiveSwitch = convertView.findViewById(R.id.isActiveSwitch);
            holder.deleteButton = convertView.findViewById(R.id.deleteButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Reminder reminder = getItem(position);

        // Set the reminder time and text
        String timeFormatted = String.format("%02d:%02d", reminder.getHour(), reminder.getMinute());
        holder.reminderTime.setText(timeFormatted);
        holder.reminderText.setText(reminder.getName());

        // Set the active status and handle its changes
        holder.isActiveSwitch.setChecked(reminder.isActive());
        holder.isActiveSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminder.setActive(isChecked);
            // Update the reminder's active status in persistent storage if necessary
        });

        // Handle deletion of a reminder
        holder.deleteButton.setOnClickListener(v -> {
            remove(reminder);
            notifyDataSetChanged();
            // Remove the reminder from persistent storage if necessary
        });

        return convertView;
    }
}