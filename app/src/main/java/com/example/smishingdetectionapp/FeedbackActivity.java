package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.smishingdetectionapp.detections.DatabaseAccess;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize the back button to go back to the previous screen
        ImageButton report_back = findViewById(R.id.feedback_back);
        report_back.setOnClickListener(v -> {
                    startActivity(new Intent(this, SettingsActivity.class));
                    finish();
        });

        // Initialize input fields and submit button
        final EditText nameInput = findViewById(R.id.nameInput);
        final EditText feedbackInput = findViewById(R.id.feedbackInput);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        final Button submitFeedbackButton = findViewById(R.id.submitFeedbackButton);

        // Disable the submit button initially
        submitFeedbackButton.setEnabled(false);

        // TextWatcher to enable the submit button only when both the name and feedback fields are filled
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userName = nameInput.getText().toString();
                String userFeedback = feedbackInput.getText().toString();
                // Enable the submit button only if both fields are filled
                submitFeedbackButton.setEnabled(!userName.isEmpty() && !userFeedback.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // Adding TextWatcher to name and feedback input fields
        nameInput.addTextChangedListener(afterTextChangedListener);
        feedbackInput.addTextChangedListener(afterTextChangedListener);

        // Submit feedback function
        submitFeedbackButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String feedback = feedbackInput.getText().toString();
            float rating = ratingBar.getRating();

            // Simulating feedback submission (You can replace this with actual database or API code)
            boolean isInserted = DatabaseAccess.sendFeedback(name, feedback, rating);
            if (isInserted) {
                // Clear input fields after successful submission
                nameInput.setText(null);
                feedbackInput.setText(null);
                ratingBar.setRating(0);
                Toast.makeText(FeedbackActivity.this, "Feedback submitted successfully!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(FeedbackActivity.this, "Failed to submit feedback. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
