package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        progressBar = findViewById(R.id.progressBar);

        int SPLASH_DISPLAY_LENGTH = 5000; // Duration in milliseconds

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start your main activity (e.g., MainActivity)
                Intent mainIntent = new Intent(LoadingActivity.this, MainActivity.class);
                LoadingActivity.this.startActivity(mainIntent);
                LoadingActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

        // Start a thread to update the progress bar
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;

                    // Update the progress bar
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });

                    try {
                        // Sleep for a short duration to simulate progress
                        Thread.sleep(50); // Adjust the duration as needed
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}