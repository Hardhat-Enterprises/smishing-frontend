package com.example.smishingdetectionapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
public class CommunityHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communityhomepage);

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Trending")); // current page
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Report"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                } else if (position == 1) {
                    // go to CommunityPostActivity
                    Intent intent = new Intent(CommunityHomeActivity.this, CommunityPostActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    // go to CommunityReportActivity >> to be created in Phase 2
                    Toast.makeText(CommunityHomeActivity.this, "Report page coming soon :)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
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

        // Bottom Navigation Bar
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
    }
}
