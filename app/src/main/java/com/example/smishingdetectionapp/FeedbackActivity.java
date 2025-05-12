package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    private static final int WORD_LIMIT = 150;
    private TextView wordCountText, wordLimitWarning, ratingPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ImageButton report_back = findViewById(R.id.feedback_back);
        EditText nameInput = findViewById(R.id.nameInput);
        EditText feedbackInput = findViewById(R.id.feedbackInput);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        Button submitFeedbackButton = findViewById(R.id.submitFeedbackButton);
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

        wordCountText.setText(getString(R.string.word_count_format, 0, WORD_LIMIT));
        submitFeedbackButton.setEnabled(false);
        submitFeedbackButton.setAlpha(0.5f);

        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userName = nameInput.getText().toString().trim();
                String userFeedback = feedbackInput.getText().toString().trim();
                int wordCount = userFeedback.isEmpty() ? 0 : userFeedback.split("\\s+").length;

                wordCountText.setText(getString(R.string.word_count_format, wordCount, WORD_LIMIT));

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
            int stars = (int) rating;

            String message = "";
            switch (stars) {
                case 1: message = "😞 Very Bad"; break;
                case 2: message = "😕 Bad"; break;
                case 3: message = "😐 Okay"; break;
                case 4: message = "🙂 Good"; break;
                case 5: message = "🤩 Excellent"; break;
            }

            showRatingPopup(message);
        });

        submitFeedbackButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String feedback = feedbackInput.getText().toString().trim();
            float rating = ratingBar.getRating();

            FeedbackMemoryStore.addFeedback(name + "|" + feedback + "|" + rating);

            nameInput.setText("");
            feedbackInput.setText("");
            ratingBar.setRating(0);
            wordCountText.setText(getString(R.string.word_count_format, 0, WORD_LIMIT));
            wordCountText.setTextColor(0xFF888888);
            wordLimitWarning.setVisibility(View.GONE);
            Toast.makeText(this, R.string.feedback_success, Toast.LENGTH_LONG).show();
        });

        viewHistoryButton.setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackHistoryActivity.class));
        });
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
                        1500
                ))
                .start();
    }
}
