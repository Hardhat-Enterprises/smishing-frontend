package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class CommunityPostActivity extends AppCompatActivity {

    private RecyclerView postsRecyclerView;
    private CommunityPostAdapter adapter;
    private List<CommunityPost> postList;
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communityposts);

        // TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Report"));
        tabLayout.getTabAt(1).select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    startActivity(new Intent(CommunityPostActivity.this, CommunityHomeActivity.class));
                    finish();
                } else if (position == 2) {
                    Toast.makeText(CommunityPostActivity.this, "Report page coming soon :)", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Search bar
        searchInput = findViewById(R.id.searchInput);
        ImageView clearSearch = findViewById(R.id.clearSearch);
        clearSearch.setOnClickListener(v -> searchInput.setText(""));

        // Display of posts
        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        postList.add(new CommunityPost("User1 • 6hrs ago", "Is this legit: 0280067670?", "This number keeps calling me. Why is it ...", 15, 1));
        postList.add(new CommunityPost("User3 • 1 day ago", "Latest Scammer called 'Albert'", "I have been scammed by this person who ...", 8, 5));

        adapter = new CommunityPostAdapter(postList);
        postsRecyclerView.setAdapter(adapter);

        // Search filter logic
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        FloatingActionButton addPostButton = findViewById(R.id.addPostButton);
        addPostButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommunityPostActivity.this, CommunityNewPost.class);
            startActivityForResult(intent, 100);
        });

        ImageButton communityBack = findViewById(R.id.community_back);
        if (communityBack != null) {
            communityBack.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
            });
        } else {
            Log.e("CommunityPostActivity", "Back button is null");
        }

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            } else {
                return false;
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String username = data.getStringExtra("username");
            String title = data.getStringExtra("posttitle");
            String description = data.getStringExtra("postdescription");
            int likes = data.getIntExtra("likes", 0);
            int comments = data.getIntExtra("comments", 0);

            CommunityPost newPost = new CommunityPost(username, title, description, likes, comments);
            postList.add(0, newPost);

            searchInput.setText(""); // Clear search to show all posts
            adapter.filter(""); // Refresh filtered list
            adapter.notifyDataSetChanged();
            postsRecyclerView.scrollToPosition(0);

            Toast.makeText(this, "New post added!", Toast.LENGTH_SHORT).show();
        }
    }
}
