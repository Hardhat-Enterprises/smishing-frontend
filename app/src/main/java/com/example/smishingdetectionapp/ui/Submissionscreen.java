package com.example.smishingdetectionapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;



public class Submissionscreen extends AppCompatActivity {

    Button exitToSettingsBtn, submitAnotherBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submission_screen); // This is your XML layout

        exitToSettingsBtn = findViewById(R.id.exitToSettingsBtn);
        submitAnotherBtn = findViewById(R.id.submitAnotherBtn);

        exitToSettingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Submissionscreen.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });

        submitAnotherBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Submissionscreen.this, ContactUsActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
