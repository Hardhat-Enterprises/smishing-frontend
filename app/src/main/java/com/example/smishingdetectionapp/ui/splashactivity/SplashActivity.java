package com.example.smishingdetectionapp.ui.splashactivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.ui.onboarding.OnboardingActivity;

/**
 * SplashActivity displays a splash screen for a fixed duration
 * before navigating to the onboarding or login flow.
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Duration (in milliseconds) of the splash screen display.
     */
    private static final int SPLASH_DELAY_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the splash screen layout
        setContentView(R.layout.activity_splash);

        // Schedule a task to run after the splash delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent for the next activity
                Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);

                // Uncomment the block below to conditionally skip onboarding
                /*
                SharedPreferences sharedPreferences =
                        getSharedPreferences("AppPreferences", MODE_PRIVATE);
                boolean isOnboardingShown = sharedPreferences
                        .getBoolean("onboarding_shown", false);

                if (!isOnboardingShown) {
                    // First-time launch: show onboarding
                    intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("onboarding_shown", true);
                    editor.apply();
                } else {
                    // Subsequent launches: go directly to login
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                */

                // Start the target activity
                startActivity(intent);
                // Finish SplashActivity so it's removed from the back stack
                finish();
            }
        }, SPLASH_DELAY_MS); // Execute after specified delay
    }
}
