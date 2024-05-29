package com.example.myapp.reminder;

public class Reminder {
    private int hour;
    private int minute;
    private boolean[] days; // Monday-Sunday
    private boolean isActive;

    private String name;

    public Reminder(int hour, int minute, boolean[] days, boolean isActive, String name) {
        this.hour = hour;
        this.minute = minute;
        this.days = days;
        this.isActive = isActive;
        this.name = name;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean[] getDays() {
        return days;
    }

    public void setDays(boolean[] days) {
        this.days = days;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

