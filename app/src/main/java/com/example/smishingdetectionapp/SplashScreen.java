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
            SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
            boolean onboardingShown = prefs.getBoolean("onboarding_shown", false);

            Intent intent;
            if (!onboardingShown) {
                intent = new Intent(SplashScreen.this, OnboardingActivity.class);
                prefs.edit().putBoolean("onboarding_shown", true).apply(); // Mark onboarding as completed
            } else {
                intent = new Intent(SplashScreen.this, OnboardingActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
