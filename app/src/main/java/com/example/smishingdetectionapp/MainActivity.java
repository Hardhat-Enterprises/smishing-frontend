package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.databinding.ActivityMainBinding;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.detections.DetectionsActivity;
import com.example.smishingdetectionapp.RadarActivity;
import com.example.smishingdetectionapp.riskmeter.RiskScannerTCActivity;
import com.example.smishingdetectionapp.notifications.NotificationPermissionDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends SharedActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private boolean isBackPressed = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_report, R.id.nav_news, R.id.nav_settings)
                .build();

        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_report) {
                startActivity(new Intent(getApplicationContext(), CommunityReportActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        Button debug_btn = findViewById(R.id.debug_btn);

        if (isGuest) {
            debug_btn.setAlpha(0.5f);
            debug_btn.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Debug mode is disabled in Guest Mode", Toast.LENGTH_SHORT).show();
            });
        } else {
            debug_btn.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, DebugActivity.class));
            });
        }
        debug_btn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DebugActivity.class)));


        Button detections_btn = findViewById(R.id.detections_btn);
        detections_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, DetectionsActivity.class));
            finish();
        });

        Button learnMoreButton = findViewById(R.id.fragment_container);
        learnMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EducationActivity.class);
            startActivity(intent);
        });

        Button scanner_btn = findViewById(R.id.scanner_btn);
        if (isGuest) {
            scanner_btn.setAlpha(0.5f);
            scanner_btn.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Scanner is disabled in Guest Mode", Toast.LENGTH_SHORT).show();
            });
        } else {
            scanner_btn.setOnClickListener(v -> {
                startActivity(new Intent(this, RiskScannerTCActivity.class));
            });
        }

        Button radarBtn = findViewById(R.id.radar_btn);
        if (isGuest) {
            radarBtn.setAlpha(0.5f);
            radarBtn.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Radar is disabled in Guest Mode", Toast.LENGTH_SHORT).show();
            });
        } else {
            radarBtn.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, RadarActivity.class));
            });
        }

        scanner_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, RiskScannerTCActivity.class));
            finish();
        });


        // Database connection
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();


        TextView infoText = findViewById(R.id.information_text);
        TextView total_count = findViewById(R.id.total_counter);

        if (isGuest) {
            infoText.setText("Welcome, Guest! You're in limited mode.\nSign in anytime to unlock full features and insights.");
        } else {
            infoText.setText("Welcome to Smishing Detection! Your real-time tool to deter and detect smishing attacks.\nYour app is ready to smish.");
            total_count.setText("" + databaseAccess.getCounter());
        }

        TextView total_count = findViewById(R.id.total_counter);
        total_count.setText("" + databaseAccess.getCounter());

        databaseAccess.close();
    }

    // Press back twice to exit
    @Override
    public void onBackPressed() {
        if (isBackPressed) {
            super.onBackPressed();
            return;
        }

        Toast.makeText(this, "press back again to exit", Toast.LENGTH_SHORT).show();
        isBackPressed = true;

        new Handler().postDelayed(() -> isBackPressed = false, 2000);
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
