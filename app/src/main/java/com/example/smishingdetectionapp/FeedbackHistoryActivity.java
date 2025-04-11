package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.graphics.Typeface;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class FeedbackHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_history);

        // Back button
        ImageButton backButton = findViewById(R.id.history_back_button);
        backButton.setOnClickListener(v -> finish());

        LinearLayout feedbackListContainer = findViewById(R.id.feedbackListContainer);
        List<String> allFeedback = FeedbackMemoryStore.getAllFeedback();

        for (String entry : allFeedback) {
            String[] parts = entry.split("\\|");
            if (parts.length == 3) {
                String name = parts[0];
                String feedback = parts[1];
                String rating = parts[2];

                // 🌟 Convert rating to stars
                int stars = (int) Float.parseFloat(rating);
                StringBuilder starsDisplay = new StringBuilder();
                for (int i = 0; i < stars; i++) starsDisplay.append("⭐️");

                // 📦 Create card
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(32, 32, 32, 32);
                card.setBackgroundResource(R.drawable.card_background);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 36);
                card.setLayoutParams(params);

                TextView user = new TextView(this);
                user.setText("👤 " + name);
                user.setTextSize(18);
                user.setTypeface(Typeface.DEFAULT_BOLD);

                TextView userFeedback = new TextView(this);
                userFeedback.setText("💬 \"" + feedback + "\"");
                userFeedback.setTextSize(16);
                userFeedback.setPadding(0, 8, 0, 8);

                TextView userRating = new TextView(this);
                userRating.setText("⭐ " + starsDisplay);
                userRating.setTextSize(16);

                card.addView(user);
                card.addView(userFeedback);
                card.addView(userRating);
                feedbackListContainer.addView(card);
            }
        }
    }
}