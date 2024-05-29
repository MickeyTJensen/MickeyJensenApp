package com.example.myapp.grocery;

public class GroceryItem {
    private String name;
    private boolean isChecked;

    public GroceryItem(String name) {
        this.name = name;
        this.isChecked = false;
    }

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}