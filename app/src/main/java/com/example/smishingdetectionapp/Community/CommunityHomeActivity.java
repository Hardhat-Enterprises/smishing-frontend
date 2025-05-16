package com.example.smishingdetectionapp.Community;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class CommunityHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communityhomepage);

        final String origin;
        String src = getIntent().getStringExtra("source");
        origin = (src == null) ? "home" : src;

        // TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Report"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 1) {
                    // Navigate to Posts
                    Intent intent = new Intent(CommunityHomeActivity.this, CommunityPostActivity.class);
                    intent.putExtra("source", origin);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                } else if (position == 2) {
                    // Navigate to Report page
                    Intent intent = new Intent(CommunityHomeActivity.this, CommunityReportActivity.class);
                    intent.putExtra("source", origin);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Back button
        ImageButton community_back = findViewById(R.id.community_back);
        if (community_back != null) {
            community_back.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
            });
        } else {
            Log.e("CommunityHomeActivity", "Back button is null");
        }

        // Bottom navigation
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_report) {
                Intent intent = new Intent(this, CommunityReportActivity.class);
                intent.putExtra("source", "home");
                startActivity(intent);
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

        // Load the trending top reported numbers and top post
        loadTrendingPost();
        loadTopReportedNumbers();
    }

    // Top post
    private void loadTrendingPost() {
        CommunityDatabaseAccess dbAccess = new CommunityDatabaseAccess(this);
        dbAccess.open();

        // Pre-loaded reported numbers while pre-loaded posts is in PostActivity
        if (dbAccess.isReportTableEmpty()) {
            dbAccess.insertOrUpdateReport("0400255019", "Preloaded: 19 reports");
            dbAccess.insertOrUpdateReport("0280067670", "Preloaded: 3 reports");
        }

        List<CommunityPost> topPosts = dbAccess.getTopLikedPosts();
        dbAccess.close();

        LinearLayout container = findViewById(R.id.topPostContainer);
        if (container == null || topPosts == null || topPosts.isEmpty()) return;

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
            cardParams.setMargins(0, 0, 0, 12);
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
            description.setPadding(0, 0, 0, 8);

            cardLayout.addView(title);
            cardLayout.addView(description);

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
    // Top reported numbers
    private void loadTopReportedNumbers() {
        CommunityDatabaseAccess dbAccess = new CommunityDatabaseAccess(this);
        dbAccess.open();
        List<CommunityReportedNumber> topReports = dbAccess.getTopReportedDetails(3);
        dbAccess.close();

        LinearLayout container = findViewById(R.id.topReportedContainer);
        if (container == null || topReports.isEmpty()) return;

        container.removeAllViews();
        container.setVisibility(View.VISIBLE);

        for (CommunityReportedNumber report : topReports) {
            LinearLayout cardLayout = new LinearLayout(this);
            cardLayout.setOrientation(LinearLayout.HORIZONTAL);
            cardLayout.setPadding(24, 24, 24, 24);
            cardLayout.setBackgroundResource(R.drawable.rounded_lightblue_card);
            cardLayout.setElevation(2);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 12);
            cardLayout.setLayoutParams(cardParams);

            ImageButton icon = new ImageButton(this);
            icon.setImageResource(R.drawable.alert);
            icon.setBackground(null);
            icon.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
            icon.setScaleType(ImageButton.ScaleType.FIT_CENTER);

            LinearLayout textLayout = new LinearLayout(this);
            textLayout.setOrientation(LinearLayout.VERTICAL);
            textLayout.setPadding(24, 0, 0, 0);

            TextView number = new TextView(this);
            number.setText(report.number + " (" + report.count + " times)");
            number.setTextSize(18);
            number.setTypeface(null, Typeface.BOLD);
            number.setTextColor(getResources().getColor(R.color.black));

            TextView date = new TextView(this);
            date.setText("last reported " + report.lastReportedDate);
            date.setTextSize(14);
            date.setTypeface(null, Typeface.ITALIC);
            date.setTextColor(getResources().getColor(R.color.black));

            textLayout.addView(number);
            textLayout.addView(date);

            cardLayout.addView(icon);
            cardLayout.addView(textLayout);
            container.addView(cardLayout);
        }
    }
}