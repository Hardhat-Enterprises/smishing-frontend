package com.example.smishingdetectionapp.Community;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommunityNewPost extends AppCompatActivity {

    private EditText titleInput, messageInput;
    private Button sharePostBtn;
    private ImageButton backButton;
    private TabLayout tabLayout;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communitynewpost);

        titleInput = findViewById(R.id.etPostTitle);
        messageInput = findViewById(R.id.etPostMessage);
        sharePostBtn = findViewById(R.id.btnSharePost);
        backButton = findViewById(R.id.community_back);
        tabLayout = findViewById(R.id.tabLayout);
        bottomNav = findViewById(R.id.bottom_navigation);

        // Share Post Button
        sharePostBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String message = messageInput.getText().toString().trim();

            if (!title.isEmpty() && !message.isEmpty()) {
                // Get user ID from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String userId = sharedPreferences.getString("user_id", "you");

                // Get current date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = sdf.format(new Date());

                Intent resultIntent = new Intent();
                resultIntent.putExtra("userId", userId);
                resultIntent.putExtra("date", currentDate);
                resultIntent.putExtra("posttitle", title);
                resultIntent.putExtra("postdescription", message);
                resultIntent.putExtra("likes", 0);       // initial like count
                resultIntent.putExtra("comments", 0);    // initial comment count
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Title and Message are required", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, CommunityPostActivity.class));
            finish();
        });

        // Tab selection
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Report"));
        tabLayout.getTabAt(1).select(); // default to Posts

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    startActivity(new Intent(CommunityNewPost.this, CommunityHomeActivity.class));
                    finish();
                } else if (position == 2) {
                    Toast.makeText(CommunityNewPost.this, "Report page coming soon :)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(this, NewsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else {
                return false;
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }
}