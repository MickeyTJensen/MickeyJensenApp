package com.example.myapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.calculator.CalculatorActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.example.myapp.grocery.GroceryListActivity;
import com.example.myapp.reminder.ReminderActivity;
import com.example.myapp.timer.TimerStopWatchActivity;
import com.example.myapp.todo.ToDoListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButtons();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        ColorStateList iconColorStateList = null; // Setting to null removes the tint
        bottomNavigationView.setItemIconTintList(iconColorStateList);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        if (item.getItemId() == R.id.navigation_home) {
                            // Handle home item click
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (item.getItemId() == R.id.navigation_calculator) {
                            Intent intent = new Intent(getApplicationContext(), CalculatorActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (item.getItemId() == R.id.navigation_notepad) {
                            // Handle notepad item click
                            return true;
                        }
                        return false;
                    }
                });
    }

    private void initButtons() {
        findViewById(R.id.buttonGroceryList).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, GroceryListActivity.class)));

        findViewById(R.id.buttonToDoList).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ToDoListActivity.class)));

        findViewById(R.id.buttonTimerStopWatch).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, TimerStopWatchActivity.class)));

        findViewById(R.id.button_reminders).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ReminderActivity.class)));
    }
}