package com.example.smishingdetectionapp.Community;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import android.content.Intent;
import android.util.Log;
import android.widget.ImageButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Typeface;

import java.util.List;

public class CommunityHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communityhomepage);

        final String origin;
        String src = getIntent().getStringExtra("source");
        if (src == null) origin = "home";
        else origin = src;


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
                    Intent intent = new Intent(CommunityHomeActivity.this,
                            CommunityPostActivity.class);
                    intent.putExtra("source", origin);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (position == 2) {
                    // launch ReportActivity
                    Intent i = new Intent(CommunityHomeActivity.this, CommunityReportActivity.class);
                    i.putExtra("source", origin);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
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
        if (community_back != null) {
            community_back.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
            });
        } else {
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

            } else if (id == R.id.nav_report) {
                Intent i = new Intent(this, CommunityReportActivity.class);
                i.putExtra("source", "home");
                startActivity(i);
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

        loadTrendingPost();
    }

    // Display the top post in card view
    private void loadTrendingPost() {
        CommunityDatabaseAccess dbAccess = new CommunityDatabaseAccess(this);
        dbAccess.open();
        List<CommunityPost> topPosts = dbAccess.getTopLikedPosts();
        dbAccess.close();

        LinearLayout container = findViewById(R.id.topPostContainer);
        if (container == null) return;

        if (topPosts != null && !topPosts.isEmpty()) {
            container.removeAllViews();
            container.setVisibility(View.VISIBLE);

            for (CommunityPost post : topPosts) {
                LinearLayout cardLayout = new LinearLayout(this);
                cardLayout.setOrientation(LinearLayout.VERTICAL);
                cardLayout.setPadding(24, 24, 24, 24);
                cardLayout.setBackgroundResource(R.drawable.rounded_lightblue_card);
                cardLayout.setElevation(2);

                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                cardParams.setMargins(0, 0, 0, 12);  // space between cards
                cardLayout.setLayoutParams(cardParams);

                // Post title
                TextView title = new TextView(this);
                title.setText(post.getPosttitle());
                title.setTextSize(18);
                title.setTypeface(null, Typeface.BOLD);
                title.setTextColor(getResources().getColor(R.color.black));
                title.setPadding(0, 0, 0, 8);

                // Post description
                TextView description = new TextView(this);
                description.setText(post.getPostdescription());
                description.setTextSize(14);
                description.setTextColor(getResources().getColor(R.color.black));
                title.setPadding(0, 0, 0, 8);

                cardLayout.addView(title);
                cardLayout.addView(description);

                // Link to individual open post
                cardLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(this, CommunityOpenPost.class);
                    intent.putExtra("postId", post.getId());
                    intent.putExtra("username", post.getUsername());
                    intent.putExtra("date", post.getDate());
                    intent.putExtra("posttitle", post.getPosttitle());
                    intent.putExtra("postdescription", post.getPostdescription());
                    intent.putExtra("likes", post.getLikes());
                    intent.putExtra("comments", post.getComments());
                    intent.putExtra("position", 0);
                    startActivity(intent);
                });

                container.addView(cardLayout);
            }
        }
    }
}