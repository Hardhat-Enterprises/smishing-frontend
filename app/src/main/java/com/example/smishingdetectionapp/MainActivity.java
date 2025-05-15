package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import java.time.LocalDate;


import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smishingdetectionapp.databinding.ActivityMainBinding;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.detections.DetectionsActivity;
import com.example.smishingdetectionapp.riskmeter.RiskScannerTCActivity;

import com.example.smishingdetectionapp.notifications.NotificationPermissionDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

public class MainActivity extends SharedActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private boolean isBackPressed;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_news, R.id.nav_settings)
                .build();

        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);

        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                nav.setActivated(true);
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        Button debug_btn = findViewById(R.id.debug_btn);
        debug_btn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DebugActivity.class)));

        Button detections_btn = findViewById(R.id.detections_btn);
        detections_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, DetectionsActivity.class));
        });


        Button learnMoreButton = findViewById(R.id.fragment_container);
        learnMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EducationActivity.class);
            startActivity(intent);
        });


        Button scanner_btn = findViewById(R.id.scanner_btn);
        scanner_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, RiskScannerTCActivity.class));

        });


        // Database connection
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        //setting counter from result
        TextView total_count;
        total_count = findViewById(R.id.total_counter);
        total_count.setText(""+databaseAccess.getCounter());


        //closing the connection
        //databaseAccess.close();
        //TODO: Add functionality for new detections.

        //Setting counter from the result
        //TextView total_count = findViewById(R.id.total_counter);
        //total_count.setText("" + databaseAccess.getCounter());

        // Closing the connection
        databaseAccess.close();

        // TapTargetView guide started in Learn more about smishing > quick guide
        boolean showGuideNow = getIntent().getBooleanExtra("showGuide", false);

        if (showGuideNow) {
            findViewById(R.id.debug_btn).post(() -> {
                new TapTargetSequence(MainActivity.this)
                    .targets(
                    TapTarget.forView(findViewById(R.id.new_detections_container), "New Detections", "This shows any newly detected smishing attempts on your device.")
                        .outerCircleColor(R.color.navy_blue)
                        .targetCircleColor(android.R.color.white)
                        .targetRadius(40)
                        .titleTextSize(22)
                        .descriptionTextSize(18)
                        .drawShadow(true)
                        .cancelable(false)
                        .transparentTarget(true),
        
                    TapTarget.forView(findViewById(R.id.total_detections_container), "Total Detections", "This shows the total number of smishing attempts detected on your device.")
                        .outerCircleColor(R.color.navy_blue)
                        .targetCircleColor(android.R.color.white)
                        .targetRadius(40)
                        .titleTextSize(22)
                        .descriptionTextSize(18)
                        .drawShadow(true)
                        .cancelable(false)
                        .transparentTarget(true),

                    TapTarget.forView(findViewById(R.id.detections_btn), "View Detections", "Tap here to view detailed records of detected smishing attempts made on your device.")
                        .outerCircleColor(R.color.navy_blue)
                        .targetCircleColor(android.R.color.white)
                        .targetRadius(31)
                        .titleTextSize(22)
                        .descriptionTextSize(18)
                        .drawShadow(true)
                        .cancelable(false)
                        .transparentTarget(true),
        
                    TapTarget.forView(findViewById(R.id.scanner_btn), "Risk Scanner", "Tap here to scan your device and assess how vulnerable it may be to smishing attacks.")
                        .outerCircleColor(R.color.navy_blue)
                        .targetCircleColor(android.R.color.white)
                        .targetRadius(31)
                        .titleTextSize(22)
                        .descriptionTextSize(18)
                        .drawShadow(true)
                        .cancelable(false)
                        .transparentTarget(true),
        
                    TapTarget.forView(findViewById(R.id.fragment_container), "Learn More", "Tap here to explore tips and tutorials to understand smishing and stay safe.")
                        .outerCircleColor(R.color.navy_blue)
                        .targetCircleColor(android.R.color.white)
                        .targetRadius(23)
                        .titleTextSize(22)
                        .descriptionTextSize(18)
                        .drawShadow(true)
                        .cancelable(false)
                        .transparentTarget(true),
        
                    TapTarget.forView(findViewById(R.id.bottom_navigation), "Navigation Bar", "This is the navigation bar. Use it to switch between the Home screen, the News section for the latest smishing updates, and the Settings page.")
                        .outerCircleColor(R.color.navy_blue)
                        .targetCircleColor(android.R.color.white)
                        .targetRadius(30)
                        .titleTextSize(22)
                        .descriptionTextSize(18)
                        .drawShadow(true)
                        .cancelable(false)
                        .transparentTarget(true)

                    )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        Toast.makeText(MainActivity.this, "You're all set to smish!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {}

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        Toast.makeText(MainActivity.this, "Guide cancelled", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
            });
        }


    }
    //tap again to exit override. only closes app if back pressed while alert is on screen
    @Override
    public void onBackPressed() {
        if(isBackPressed)
        {
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, "press back again to exit", Toast.LENGTH_SHORT).show();
        isBackPressed = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isBackPressed = false;
            }
        }, 2000);

    }

    private boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void showNotificationPermissionDialog() {
        NotificationPermissionDialogFragment dialogFragment = new NotificationPermissionDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "notificationPermission");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
