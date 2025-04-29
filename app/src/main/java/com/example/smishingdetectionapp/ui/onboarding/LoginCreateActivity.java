package com.example.smishingdetectionapp.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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
    }


}
