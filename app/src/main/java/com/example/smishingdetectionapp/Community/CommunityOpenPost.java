package com.example.smishingdetectionapp.Community;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.Random;

public class CommunityOpenPost extends AppCompatActivity {

    private TextView titleText, descText, usernameText, timestampText, likesText, commentsText;
    private EditText commentInput;
    private Button addCommentBtn;
    private ImageButton backButton;
    private ImageView likeIcon;
    private RecyclerView commentRecycler;
    private TabLayout tabLayout;
    private BottomNavigationView bottomNav;
    private List<CommunityComment> commentList = new ArrayList<>();
    private CommunityDatabaseAccess dbAccess;

    private int likeCount = 15;
    private int commentCount = 1;
    private CommunityCommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communityopenpost);

        // UI components
        usernameText = findViewById(R.id.usernameText);
        timestampText = findViewById(R.id.dateText);
        titleText = findViewById(R.id.titleText);
        descText = findViewById(R.id.descText);
        likesText = findViewById(R.id.likes);
        commentInput = findViewById(R.id.commentInput);
        commentsText = findViewById(R.id.comments);
        addCommentBtn = findViewById(R.id.addCommentBtn);
        commentRecycler = findViewById(R.id.commentRecycler);
        tabLayout = findViewById(R.id.tabLayout);
        bottomNav = findViewById(R.id.bottom_navigation);
        likeIcon = findViewById(R.id.likeIcon);
        backButton = findViewById(R.id.community_back);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String date = intent.getStringExtra("date");
        String title = intent.getStringExtra("posttitle");
        String description = intent.getStringExtra("postdescription");
        int likes = intent.getIntExtra("likes", 0);
        int comments = intent.getIntExtra("comments", 0);

        int postId = intent.getIntExtra("postId", -1);

        if (postId != -1) {
            dbAccess = new CommunityDatabaseAccess(this);
            dbAccess.open();

            commentList = dbAccess.getCommentsByPostId(postId);
        }

        usernameText.setText(username != null ? username : "Unknown");
        timestampText.setText(date != null ? date : "Unknown");
        titleText.setText(title != null ? title : "");
        descText.setText(description != null ? description : "");
        likesText.setText(likes + " likes");
        commentsText.setText(comments + " comments");
        commentCount = comments;
        likeCount = likes;

        commentRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommunityCommentAdapter(this, commentList, postId);
        commentRecycler.setAdapter(adapter);

        // Like button
        likeIcon.setOnClickListener(v -> {
            likeCount++;
            likesText.setText(likeCount + " likes");

            if (postId != -1) {
                dbAccess.updatePostLikes(postId, likeCount);
            }
        });

        // Add comment
        addCommentBtn.setOnClickListener(v -> {
            String commentTextStr = commentInput.getText().toString().trim();
            if (!commentTextStr.isEmpty()) {
                // Get current timestamp
                String timestamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                // assign unique user ID
                String userId = getOrCreateUserId();

                // Create and insert new comment
                CommunityComment newComment = new CommunityComment(-1, postId, userId, timestamp, commentTextStr);
                dbAccess.insertComment(newComment);

                // Update local list and adapter
                commentList.add(newComment);
                adapter.notifyItemInserted(commentList.size() - 1);

                // Clear input
                commentInput.setText("");

                // Update comment count
                commentCount++;
                commentsText.setText(commentCount + " comments");
                dbAccess.updatePostComments(postId, commentCount);

            } else {
                Toast.makeText(this, "Please tell us something", Toast.LENGTH_SHORT).show();
            }
        });

        // Tab navigation
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Report"));
        tabLayout.clearOnTabSelectedListeners();
        tabLayout.getTabAt(1).select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    startActivity(new Intent(CommunityOpenPost.this, CommunityHomeActivity.class));
                    finish();
                } else if (position == 2) {
                    Toast.makeText(CommunityOpenPost.this, "Report page coming soon :)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Back button
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                startActivity(new Intent(this, CommunityPostActivity.class));
                finish();
            });
        } else {
            Log.e("CommunityOpenPost", "Back button is null");
        }

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

    private String getOrCreateUserId() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        if (userId == null) {
            userId = "User" + new Random().nextInt(10000);
            prefs.edit().putString("user_id", userId).apply();
        }
        return userId;
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
        resultIntent.putExtra("updatedComments", commentCount);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}