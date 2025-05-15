package com.example.smishingdetectionapp;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.example.smishingdetectionapp.ui.onboarding.OnboardingActivity;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // Splash screen duration in milliseconds
    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        imageview = findViewById(R.id.imageview);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Start splash animation
        ObjectAnimator animator = (ObjectAnimator) AnimatorInflater
                .loadAnimator(this, R.animator.smishing_detection_logo_animator);
        animator.setTarget(imageview);
        animator.start();

        // Navigate after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

            boolean isGuest = prefs.getBoolean("isGuest", false);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
            //boolean onboardingShown = prefs.getBoolean("onboarding_shown", false); uncomment when finsihed
            boolean onboardingShown = false; // <<<< FORCE it to always be false for testing

            Intent intent;

            if (!onboardingShown) {
                // First-time user → show onboarding
                intent = new Intent(SplashScreen.this, OnboardingActivity.class);
            } else if (isGuest || isLoggedIn) {
                // Already logged in or using Guest Mode
                intent = new Intent(SplashScreen.this, MainActivity.class);
            } else {
                // Show login page
                intent = new Intent(SplashScreen.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}