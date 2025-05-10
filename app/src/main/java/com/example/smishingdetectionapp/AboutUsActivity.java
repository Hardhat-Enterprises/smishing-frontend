package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        //back button
        ImageButton backButton = findViewById(R.id.about_back);
        backButton.setOnClickListener(v -> {
            // Navigate back to SettingsActivity
            Intent intent = new Intent(AboutUsActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();

    });
    }
}
