package com.example.smishingdetectionapp;  // Ensure this matches your package

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class AboutMeActivity extends SharedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);  // Ensure this layout exists

        // Handle the back button click
        ImageButton backButton = findViewById(R.id.about_back);
        backButton.setOnClickListener(v -> {
            // Navigate back to SettingsActivity
            Intent intent = new Intent(AboutMeActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();  // Optional: Finish the current activity
        });
    }
}
