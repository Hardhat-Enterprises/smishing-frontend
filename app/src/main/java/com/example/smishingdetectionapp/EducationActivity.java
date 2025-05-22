package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EducationActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

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

        // Back button returns to previous screen
        ImageButton backButton = findViewById(R.id.education_back);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        // Launch Scan URL Activity
        Button scanUrlButton = findViewById(R.id.scan_url_button);
        scanUrlButton.setOnClickListener(v -> {
            Intent intent = new Intent(EducationActivity.this, VirusTotalScanner.class);
            startActivity(intent);
        });

        // Setup embedded YouTube WebView
        WebView youtubeWebView = findViewById(R.id.youtubeWebView);
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        youtubeWebView.setWebViewClient(new WebViewClient());
        youtubeWebView.loadUrl("https://www.youtube.com/embed/ZOZGQeG8avQ");

        // Launch quizzes
        Button quizButton = findViewById(R.id.quiz_button);
        quizButton.setOnClickListener(v -> {
            Intent intent = new Intent(EducationActivity.this, QuizesActivity.class);
            startActivity(intent);
        });
        // Quick guide button
        Button tutorialBtn = findViewById(R.id.tutorialBtn);
        tutorialBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EducationActivity.this, MainActivity.class);
            intent.putExtra("showGuide", true); 
            startActivity(intent);
        });
    }
}
