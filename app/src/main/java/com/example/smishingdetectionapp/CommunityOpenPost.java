package com.example.smishingdetectionapp;

import android.content.Intent;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class CommunityOpenPost extends AppCompatActivity {

    private TextView titleText, descText, usernameText, likesText, commentsText;
    private EditText commentInput;
    private Button addCommentBtn;
    private ImageButton backButton;
    private ImageView likeIcon;
    private RecyclerView commentRecycler;
    private TabLayout tabLayout;
    private BottomNavigationView bottomNav;

    private int likeCount = 15; // mock initial like count
    private int commentCount = 1; // mock initial comment count
    private ArrayList<String> commentList = new ArrayList<>();
    private CommunityCommentAdapter adapter;

    // link to the communityopenpost design
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communityopenpost);

        usernameText = findViewById(R.id.usernameText);
        titleText = findViewById(R.id.titleText);
        descText = findViewById(R.id.descText);
        likesText = findViewById(R.id.likes);
        commentInput = findViewById(R.id.commentInput);
        commentsText = findViewById(R.id.comments);
        commentsText.setText(commentCount + " comments");
        addCommentBtn = findViewById(R.id.addCommentBtn);
        commentRecycler = findViewById(R.id.commentRecycler);
        tabLayout = findViewById(R.id.tabLayout);
        bottomNav = findViewById(R.id.bottom_navigation);
        likeIcon = findViewById(R.id.likeIcon);
        backButton = findViewById(R.id.community_back);

        // Mock post details
        usernameText.setText("User1 • 6hrs ago");
        titleText.setText("Is this legit: 0280 067 670?");
        descText.setText("This number keeps calling me. Why is it that this app does not block this number? Anyone else facing the same issue. It is very annoying but I also do not know if it is legit.");
        likesText.setText(likeCount + " likes");

        // Mock comments
        commentList.add("User22 • 2hrs ago\nI have just received a call from this number today! We should report on the app!");

        commentRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommunityCommentAdapter(commentList);
        commentRecycler.setAdapter(adapter);

        // Like button
        likeIcon.setOnClickListener(v -> {
            likeCount++;
            likesText.setText(likeCount + " likes");
        });

        // Add comment button
        addCommentBtn.setOnClickListener(v -> {
            String comment = commentInput.getText().toString().trim();
            if (!comment.isEmpty()) {
                commentList.add("You • now\n" + comment); //display new current comment
                adapter.notifyItemInserted(commentList.size() - 1);
                commentInput.setText("");
                commentCount++;
                commentsText.setText(commentCount + " comments"); // increase number of comments


            } else {
                Toast.makeText(this, "Please tell us something", Toast.LENGTH_SHORT).show();
            }
        });

        // Tab Navigation
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Report"));
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
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Back button to the posts page
        ImageButton communityBack = findViewById(R.id.community_back);
        if (communityBack != null) {
            communityBack.setOnClickListener(v -> {
                startActivity(new Intent(this, CommunityPostActivity.class));
                finish();
            });
        } else {
            Log.e("CommunityOpenPost", "Back button is null");
        }

        // Bottom Navigation
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
