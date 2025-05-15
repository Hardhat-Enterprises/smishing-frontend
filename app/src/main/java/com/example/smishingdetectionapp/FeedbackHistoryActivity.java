package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import java.util.List;

public class FeedbackHistoryActivity extends AppCompatActivity {

    private LinearLayout feedbackListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_history);

        ImageButton backButton = findViewById(R.id.feedback_back);
        backButton.setOnClickListener(v -> finish());

        feedbackListContainer = findViewById(R.id.feedbackListContainer);

        displayFeedbackHistory();
    }

    private void displayFeedbackHistory() {
        List<String> feedbackList = FeedbackMemoryStore.getFeedbackHistory();

        if (feedbackList.isEmpty()) {
            TextView noFeedbackText = new TextView(this);
            noFeedbackText.setText("No feedback found.");
            noFeedbackText.setTextSize(16);
            noFeedbackText.setPadding(16, 24, 16, 24);
            feedbackListContainer.addView(noFeedbackText);
            return;
        }

        for (String entry : feedbackList) {
            // Expecting format: name|message|rating
            String[] parts = entry.split("\\|");
            if (parts.length < 3) continue;

            String name = parts[0];
            String message = parts[1];
            String rating = parts[2];

            // Create container layout for card
            LinearLayout cardLayout = new LinearLayout(this);
            cardLayout.setOrientation(LinearLayout.VERTICAL);
            cardLayout.setPadding(24, 24, 24, 24);
            cardLayout.setBackgroundResource(R.drawable.card_background);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 24);
            cardLayout.setLayoutParams(params);

            // Name
            TextView nameText = new TextView(this);
            nameText.setText("👤 " + name);
            nameText.setTextSize(16);
            nameText.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            nameText.setPadding(0, 0, 0, 8);
            cardLayout.addView(nameText);

            // Message
            TextView messageText = new TextView(this);
            messageText.setText("💬 \"" + message + "\"");
            messageText.setTextSize(15);
            messageText.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            cardLayout.addView(messageText);

            // Star Rating
            TextView starsText = new TextView(this);
            try {
                int stars = (int) Float.parseFloat(rating);
                StringBuilder starsBuilder = new StringBuilder();
                for (int i = 0; i < stars; i++) {
                    starsBuilder.append("⭐");
                }
                starsText.setText(starsBuilder.toString());
            } catch (NumberFormatException e) {
                starsText.setText("⭐");
            }
            starsText.setPadding(0, 12, 0, 0);
            cardLayout.addView(starsText);

            feedbackListContainer.addView(cardLayout);
        }
    }
}
