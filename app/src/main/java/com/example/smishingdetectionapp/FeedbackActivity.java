package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FeedbackActivity extends AppCompatActivity {

    private static final int WORD_LIMIT = 150;
    private TextView wordCountText, wordLimitWarning, ratingPopup;
    private EditText nameInput, feedbackInput;
    private RatingBar ratingBar;
    private Button submitFeedbackButton;
    private int editingIndex = -1;
    private String originalEntry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_settings);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (menuItem.getItemId() == R.id.nav_report) {
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
                Intent intent1 = new Intent(getApplicationContext(), SettingsActivity.class);
                intent1.putExtra("from_navigation", true);
                startActivity(intent1);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });



        ImageButton report_back = findViewById(R.id.feedback_back);
        nameInput = findViewById(R.id.nameInput);
        feedbackInput = findViewById(R.id.feedbackInput);
        ratingBar = findViewById(R.id.ratingBar);
        submitFeedbackButton = findViewById(R.id.submitFeedbackButton);
        Button viewHistoryButton = findViewById(R.id.viewHistoryButton);
        ratingPopup = findViewById(R.id.ratingPopup);
        wordCountText = findViewById(R.id.wordCountText);
        wordLimitWarning = findViewById(R.id.wordLimitWarning);

        report_back.setOnClickListener(v -> finish());

        feedbackInput.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                InputType.TYPE_TEXT_FLAG_AUTO_CORRECT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        feedbackInput.setGravity(Gravity.TOP | Gravity.START);

        wordCountText.setText("Words: 0 / " + WORD_LIMIT);
        submitFeedbackButton.setEnabled(false);
        submitFeedbackButton.setAlpha(0.5f);

        TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userName = nameInput.getText().toString().trim();
                String userFeedback = feedbackInput.getText().toString().trim();
                int wordCount = userFeedback.isEmpty() ? 0 : userFeedback.split("\\s+").length;

                wordCountText.setText("Words: " + wordCount + " / " + WORD_LIMIT);

                if (wordCount > WORD_LIMIT) {
                    wordCountText.setTextColor(0xFFFF4444);
                    wordLimitWarning.setVisibility(View.VISIBLE);
                } else {
                    wordCountText.setTextColor(0xFF888888);
                    wordLimitWarning.setVisibility(View.GONE);
                }

                boolean enableSubmit = !userName.isEmpty() && !userFeedback.isEmpty() && wordCount <= WORD_LIMIT;
                submitFeedbackButton.setEnabled(enableSubmit);
                submitFeedbackButton.setAlpha(enableSubmit ? 1f : 0.5f);
            }
        };

        nameInput.addTextChangedListener(textWatcher);
        feedbackInput.addTextChangedListener(textWatcher);

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            String message = "";
            switch ((int) rating) {
                case 1: message = "😞 Very Bad"; break;
                case 2: message = "😕 Bad"; break;
                case 3: message = "😐 Okay"; break;
                case 4: message = "🙂 Good"; break;
                case 5: message = "🤩 Excellent"; break;
                default: message = "";
            }
            showRatingPopup(message);
        });

        submitFeedbackButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String feedback = feedbackInput.getText().toString().trim();
            float rating = ratingBar.getRating();
            String entry = name + "|" + feedback + "|" + rating;

            if (editingIndex >= 0 && originalEntry != null) {
                FeedbackMemoryStore.updateFeedback(originalEntry, entry);
                Toast.makeText(FeedbackActivity.this, "Feedback updated", Toast.LENGTH_SHORT).show();
            } else {
                FeedbackMemoryStore.addFeedback(entry);
                Toast.makeText(FeedbackActivity.this, R.string.feedback_success, Toast.LENGTH_SHORT).show();
            }

            nameInput.setText("");
            feedbackInput.setText("");
            ratingBar.setRating(0);
            wordCountText.setText("Words: 0 / " + WORD_LIMIT);
            wordCountText.setTextColor(0xFF888888);
            wordLimitWarning.setVisibility(View.GONE);
            submitFeedbackButton.setEnabled(false);
            submitFeedbackButton.setAlpha(0.5f);
            editingIndex = -1;
            originalEntry = null;
        });

        viewHistoryButton.setOnClickListener(v -> {
            startActivity(new Intent(FeedbackActivity.this, FeedbackHistoryActivity.class));
        });

        Intent intent = getIntent();
        if (intent.hasExtra("editName") && intent.hasExtra("editMessage") && intent.hasExtra("editRating") && intent.hasExtra("originalEntry")) {
            nameInput.setText(intent.getStringExtra("editName"));
            feedbackInput.setText(intent.getStringExtra("editMessage"));
            ratingBar.setRating(Float.parseFloat(intent.getStringExtra("editRating")));
            originalEntry = intent.getStringExtra("originalEntry");
            editingIndex = FeedbackMemoryStore.getFeedbackHistory().indexOf(originalEntry);
            submitFeedbackButton.setText("Update Feedback");
        }
    }

    private void showRatingPopup(String message) {
        ratingPopup.setText(message);
        ratingPopup.setAlpha(0f);
        ratingPopup.setVisibility(View.VISIBLE);

        ratingPopup.animate()
                .alpha(1f)
                .translationYBy(-30f)
                .setDuration(300)
                .withEndAction(() -> new Handler().postDelayed(
                        () -> ratingPopup.animate()
                                .alpha(0f)
                                .translationYBy(30f)
                                .setDuration(300)
                                .withEndAction(() -> ratingPopup.setVisibility(View.GONE))
                                .start(),
                        1500))
                .start();
    }
}