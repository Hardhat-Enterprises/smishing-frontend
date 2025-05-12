package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class FeedbackHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_history);

        // ⬅️ Back Button
        ImageButton backButton = findViewById(R.id.history_back_button);
        backButton.setOnClickListener(v -> finish());

        // 📄 Feedback List
        LinearLayout feedbackListContainer = findViewById(R.id.feedbackListContainer);
        List<String> allFeedback = FeedbackMemoryStore.getAllFeedback();

        for (String entry : allFeedback) {
            String[] parts = entry.split("\\|");
            if (parts.length == 3) {
                String name = parts[0].trim();
                String feedback = parts[1].trim();
                float rating = Float.parseFloat(parts[2]);

                // ⭐ Create card
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

                // 🧑 Name
                TextView tvName = new TextView(this);
                tvName.setText(name);
                tvName.setTextSize(18);
                tvName.setTypeface(Typeface.DEFAULT_BOLD);

                // 💬 Feedback
                TextView tvFeedback = new TextView(this);
                tvFeedback.setText("“" + feedback + "”");
                tvFeedback.setTextSize(16);
                tvFeedback.setPadding(0, 6, 0, 6);

                // ⭐ Rating
                TextView tvRating = new TextView(this);
                StringBuilder starDisplay = new StringBuilder();
                for (int i = 0; i < (int) rating; i++) {
                    starDisplay.append("⭐");
                }
                tvRating.setText(starDisplay.toString());
                tvRating.setTextSize(16);

                // 🔗 Add views
                card.addView(tvName);
                card.addView(tvFeedback);
                card.addView(tvRating);
                feedbackListContainer.addView(card);
            }
        }
    }
}
