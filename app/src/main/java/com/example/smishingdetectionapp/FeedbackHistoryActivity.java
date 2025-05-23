package com.example.smishingdetectionapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FeedbackHistoryActivity extends AppCompatActivity {
    private LinearLayout feedbackListContainer;
    private Button selectToggleBtn, deleteSelectedBtn, cancelSelectBtn, editSelectedBtn;
    private boolean selectionMode = false;
    private final List<View> selectedCards = new ArrayList<>();
    private final List<String> selectedEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_history);

        ImageButton backButton = findViewById(R.id.feedback_back);
        feedbackListContainer = findViewById(R.id.feedbackListContainer);
        selectToggleBtn = findViewById(R.id.selectToggleBtn);
        deleteSelectedBtn = findViewById(R.id.deleteSelectedBtn);
        cancelSelectBtn = findViewById(R.id.cancelSelectBtn);
        editSelectedBtn = findViewById(R.id.editSelectedBtn);

        backButton.setOnClickListener(v -> finish());

        selectToggleBtn.setVisibility(View.VISIBLE);
        deleteSelectedBtn.setVisibility(View.GONE);
        cancelSelectBtn.setVisibility(View.GONE);
        editSelectedBtn.setVisibility(View.GONE);

        selectToggleBtn.setOnClickListener(v -> {
            selectionMode = true;
            selectToggleBtn.setVisibility(View.GONE);
            deleteSelectedBtn.setVisibility(View.VISIBLE);
            cancelSelectBtn.setVisibility(View.VISIBLE);
            editSelectedBtn.setVisibility(View.VISIBLE);
            displayFeedbackHistory();
        });

        cancelSelectBtn.setOnClickListener(v -> {
            selectionMode = false;
            selectedCards.clear();
            selectedEntries.clear();
            selectToggleBtn.setVisibility(View.VISIBLE);
            deleteSelectedBtn.setVisibility(View.GONE);
            cancelSelectBtn.setVisibility(View.GONE);
            editSelectedBtn.setVisibility(View.GONE);
            displayFeedbackHistory();
        });

        deleteSelectedBtn.setOnClickListener(v -> {
            for (String entry : selectedEntries) {
                FeedbackMemoryStore.removeFeedback(entry);
            }
            selectedCards.clear();
            selectedEntries.clear();
            displayFeedbackHistory();
        });

        editSelectedBtn.setOnClickListener(v -> {
            if (!selectedEntries.isEmpty()) {
                String[] parts = selectedEntries.get(0).split("\\|");
                if (parts.length == 3) {
                    Intent intent = new Intent(this, FeedbackActivity.class);
                    intent.putExtra("editName", parts[0]);
                    intent.putExtra("editMessage", parts[1]);
                    intent.putExtra("editRating", parts[2]);
                    intent.putExtra("originalEntry", selectedEntries.get(0));
                    startActivity(intent);
                    finish();
                }
            }
        });

        displayFeedbackHistory();
    }

    private void displayFeedbackHistory() {
        feedbackListContainer.removeAllViews();
        List<String> feedbackList = FeedbackMemoryStore.getFeedbackHistory();

        for (String entry : feedbackList) {
            String[] parts = entry.split("\\|");
            if (parts.length != 3) continue;

            View cardLayout = getLayoutInflater().inflate(R.layout.recent_feedback_item, feedbackListContainer, false);
            TextView tvUsername = cardLayout.findViewById(R.id.tvUsername);
            TextView tvMessage = cardLayout.findViewById(R.id.tvMessage);
            LinearLayout ratingStars = cardLayout.findViewById(R.id.ratingStars);

            tvUsername.setText("👤 " + parts[0]);
            tvMessage.setText("💬 \"" + parts[1] + "\"");

            float rating = Float.parseFloat(parts[2]);
            ratingStars.removeAllViews();
            for (int i = 0; i < rating; i++) {
                TextView star = new TextView(this);
                star.setText("⭐");
                star.setTextSize(18f);
                star.setTextColor(Color.parseColor("#FFD700"));
                ratingStars.addView(star);
            }

            if (selectionMode) {
                cardLayout.setBackgroundResource(selectedEntries.contains(entry) ? R.drawable.card_selected : R.drawable.card_background);
                cardLayout.setOnClickListener(v -> {
                    if (selectedEntries.contains(entry)) {
                        selectedEntries.remove(entry);
                        selectedCards.remove(cardLayout);
                        cardLayout.setBackgroundResource(R.drawable.card_background);
                    } else {
                        selectedEntries.clear();
                        selectedCards.clear();
                        selectedEntries.add(entry);
                        selectedCards.add(cardLayout);
                        displayFeedbackHistory();
                    }
                });
            } else {
                cardLayout.setBackgroundResource(R.drawable.card_background);
                cardLayout.setOnClickListener(null);
            }

            feedbackListContainer.addView(cardLayout);
        }
    }
}
