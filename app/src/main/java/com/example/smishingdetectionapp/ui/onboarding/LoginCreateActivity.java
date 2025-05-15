package com.example.smishingdetectionapp.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.ui.Register.RegisterMain;
import com.example.smishingdetectionapp.ui.login.LoginActivity;

public class LoginCreateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout
        setContentView(R.layout.login_create_page);

        // Reference the buttons from the layout
        Button signUpButton = findViewById(R.id.signUpButton);
        Button loginButton = findViewById(R.id.loginButton);
        Button guestModeButton = findViewById(R.id.guestModeButton);


        // click listener for the "Sign Up" button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the registration screen
                Intent intent = new Intent(LoginCreateActivity.this, RegisterMain.class);
                startActivity(intent);
            }
        });

        // click listener for the "Log In" button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the login screen
                Intent intent = new Intent(LoginCreateActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Button guestButton = findViewById(R.id.guestModeButton);
        guestButton.setOnClickListener(view -> {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("isGuest", true).apply();

            Intent intent = new Intent(LoginCreateActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


    }


}