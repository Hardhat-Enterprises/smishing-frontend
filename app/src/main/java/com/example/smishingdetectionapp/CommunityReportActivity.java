package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class CommunityReportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_report);

        // 1️⃣ TabLayout: add 3 tabs and select “Report”
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Report"));
        tabLayout.getTabAt(2).select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos == 0) {
                    // go back to trending
                    startActivity(new Intent(CommunityReportActivity.this, CommunityHomeActivity.class));
                    overridePendingTransition(0,0);
                    finish();
                } else if (pos == 1) {
                    // go to posts
                    startActivity(new Intent(CommunityReportActivity.this, CommunityPostActivity.class));
                    overridePendingTransition(0,0);
                    finish();
                }
                // pos == 2 → stay on report
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });


        // Back Button
        ImageButton community_back = findViewById(R.id.community_back);
        // Check if the back button is initialized properly
        if (community_back != null) {
            // Set an onClick listener to handle the back button's behavior
            community_back.setOnClickListener(v -> {
                // Start SettingsActivity when back button is pressed
                startActivity(new Intent(this, SettingsActivity.class));
                // Close the current activity
                finish();
            });
        } else {
            // Log an error if the back button is null
            Log.e("NotificationActivity", "Back button is null");
        }


        // 2️⃣ BottomNavigationView: identical to CommunityHomeActivity’s
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            Intent intent;
            int id = item.getItemId();
            if      (id == R.id.nav_home)     intent = new Intent(this, MainActivity.class);
            else if (id == R.id.nav_news)     intent = new Intent(this, NewsActivity.class);
            else if (id == R.id.nav_settings) intent = new Intent(this, SettingsActivity.class);
            else return false;
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
            return true;
        });

        // 3️⃣ Form logic: validate & toast
        EditText etPhone   = findViewById(R.id.etPhoneNumber);
        EditText etMessage = findViewById(R.id.etMessageContent);
        Button btnReport   = findViewById(R.id.btnReportProtect);

        btnReport.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String msg   = etMessage.getText().toString().trim();
            if (phone.isEmpty() || msg.isEmpty()) {
                Toast.makeText(this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Report submitted. Thank you!", Toast.LENGTH_LONG).show();
                // TODO: send to backend
            }
        });
    }
}