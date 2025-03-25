package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.detections.DatabaseAccess;

public class FeedbackActivity extends AppCompatActivity {

    private static final int WORD_LIMIT = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ImageButton report_back = findViewById(R.id.feedback_back);
        final EditText nameInput = findViewById(R.id.nameInput);
        final EditText feedbackInput = findViewById(R.id.feedbackInput);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        final Button submitFeedbackButton = findViewById(R.id.submitFeedbackButton);

        // Word counter
        final TextView wordCountText = new TextView(this);
        wordCountText.setText(getString(R.string.word_count_format, 0, WORD_LIMIT));
        wordCountText.setTextSize(14);
        wordCountText.setTextColor(0xFF888888);

        // Word limit warning
        final TextView wordLimitWarning = new TextView(this);
        wordLimitWarning.setText(getString(R.string.word_limit_warning));
        wordLimitWarning.setTextColor(0xFFFF4444); // Red
        wordLimitWarning.setTextSize(13);
        wordLimitWarning.setPadding(0, 4, 0, 0);
        wordLimitWarning.setVisibility(TextView.GONE);

        // Container aligned to top-right
        LinearLayout topRightWrapper = new LinearLayout(this);
        topRightWrapper.setOrientation(LinearLayout.VERTICAL);
        topRightWrapper.setGravity(Gravity.END);
        topRightWrapper.setPadding(0, 50, 32, 0); // Adjust top & right padding as needed
        topRightWrapper.addView(wordCountText);
        topRightWrapper.addView(wordLimitWarning);

        // Add to root layout
        FrameLayout rootLayout = findViewById(android.R.id.content);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.TOP | Gravity.END;
        topRightWrapper.setLayoutParams(params);
        rootLayout.addView(topRightWrapper);

        // Enable autocorrect
        feedbackInput.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

        // Disable submit button initially
        submitFeedbackButton.setEnabled(false);
        submitFeedbackButton.setAlpha(0.5f);

        // Back button
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // TextWatcher
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userName = nameInput.getText().toString().trim();
                String userFeedback = feedbackInput.getText().toString().trim();
                int wordCount = userFeedback.isEmpty() ? 0 : userFeedback.split("\\s+").length;

                wordCountText.setText(getString(R.string.word_count_format, wordCount, WORD_LIMIT));

                if (wordCount > WORD_LIMIT) {
                    wordCountText.setTextColor(0xFFFF4444);
                    wordLimitWarning.setVisibility(TextView.VISIBLE);
                } else {
                    wordCountText.setTextColor(0xFF888888);
                    wordLimitWarning.setVisibility(TextView.GONE);
                }

                boolean enableSubmit = !userName.isEmpty() && !userFeedback.isEmpty() && wordCount <= WORD_LIMIT;
                submitFeedbackButton.setEnabled(enableSubmit);
                submitFeedbackButton.setAlpha(enableSubmit ? 1f : 0.5f);
            }

            @Override public void afterTextChanged(Editable s) {}
        };

        nameInput.addTextChangedListener(textWatcher);
        feedbackInput.addTextChangedListener(textWatcher);

        // Submit logic
        submitFeedbackButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String feedback = feedbackInput.getText().toString();
            float rating = ratingBar.getRating();

            boolean isInserted = DatabaseAccess.sendFeedback(name, feedback, rating);
            if (isInserted) {
                nameInput.setText(null);
                feedbackInput.setText(null);
                ratingBar.setRating(0);
                wordCountText.setText(getString(R.string.word_count_format, 0, WORD_LIMIT));
                wordCountText.setTextColor(0xFF888888);
                wordLimitWarning.setVisibility(TextView.GONE);
                Toast.makeText(FeedbackActivity.this, getString(R.string.feedback_success), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(FeedbackActivity.this, getString(R.string.feedback_failure), Toast.LENGTH_LONG).show();
            }
        });
    }
}
