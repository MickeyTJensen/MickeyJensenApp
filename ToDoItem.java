package com.example.myapp.todo;

public class ToDoItem {
    private String name;
    private boolean isChecked;

    public ToDoItem(String name) {
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