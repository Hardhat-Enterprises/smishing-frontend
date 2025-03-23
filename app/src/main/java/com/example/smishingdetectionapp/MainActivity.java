package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smishingdetectionapp.databinding.ActivityMainBinding;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.detections.DetectionsActivity;
import com.example.smishingdetectionapp.notifications.NotificationPermissionDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends SharedActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 1. Load user preference for dark mode
        SharedPreferences prefs = getSharedPreferences("user_settings", MODE_PRIVATE);
        boolean isDarkModeEnabled = prefs.getBoolean("dark_mode_enabled", false);

        // 2. Apply the mode before inflating any layouts
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // 3. Proceed with normal onCreate flow
        super.onCreate(savedInstanceState);

        // Inflate and set the layout
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Now continue with your existing setup code
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_news, R.id.nav_settings
        ).build();

        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
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
        debug_btn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DebugActivity.class)));

        Button detections_btn = findViewById(R.id.detections_btn);
        detections_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, DetectionsActivity.class));
            finish();
        });

        Button learnMoreButton = findViewById(R.id.learn_more_btn);
        learnMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EducationActivity.class);
            startActivity(intent);
        });

        // Database connection
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        // Setting counter from the result
        TextView total_count = findViewById(R.id.total_counter);
        total_count.setText("" + databaseAccess.getCounter());

        // Closing the connection
        databaseAccess.close();
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
