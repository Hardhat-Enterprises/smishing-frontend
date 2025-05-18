package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends AppCompatActivity {

    private RecyclerView faqRecyclerView;
    private FaqAdapter faqAdapter;
    private List<FaqItem> faqList;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        // Handle system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });

        // Back button logic
        ImageButton backBtn = findViewById(R.id.faq_back);
        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(FaqActivity.this, HelpActivity.class));
            finish();
        });


        // FAQ list setup
        faqRecyclerView = findViewById(R.id.faqRecyclerView);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        faqList = new ArrayList<>();
        faqList.add(new FaqItem("What is Smishing?", "Smishing is a phishing attack via SMS to steal your data."));
        faqList.add(new FaqItem("How does the app detect smishing?", "It uses AI and NLP to analyze SMS patterns."));
        faqList.add(new FaqItem("Can I report a suspicious message?", "Yes, use the report button in message view."));
        faqList.add(new FaqItem("Is it available on iOS?", "Yes, the app is available on both Android and iOS."));

        faqAdapter = new FaqAdapter(faqList);
        faqRecyclerView.setAdapter(faqAdapter);

        // navigation bar
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
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
                overridePendingTransition(0,0);
                finish();
                return true;

            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
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
