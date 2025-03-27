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

public class EducationActivity extends AppCompatActivity {
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);


        ImageButton backButton = findViewById(R.id.education_back);
        backButton.setOnClickListener(v -> finish());


        WebView youtubeWebView = findViewById(R.id.youtubeWebView);
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        youtubeWebView.setWebViewClient(new WebViewClient());
        youtubeWebView.loadUrl("https://www.youtube.com/embed/ZOZGQeG8avQ");


        Button quizButton = findViewById(R.id.quiz_button);
        quizButton.setOnClickListener(v -> {
            Intent intent = new Intent(EducationActivity.this, QuizesActivity.class);
            startActivity(intent);
        });

    }
}
