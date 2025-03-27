package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TermsAndConditionsActivity extends SharedActivity {

    private Button backButton;
    private ScrollView termsScrollView;
    private TextView termsTextView;
    private boolean hasScrolledToBottom = false; // Flag to track if user has scrolled to the bottom

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions); // Ensure XML file exists and matches this name

        // Initialize views
        backButton = findViewById(R.id.accept_button);
        termsScrollView = findViewById(R.id.terms_scroll_view);
        termsTextView = findViewById(R.id.terms_text_view);

        // Set formatted terms and conditions text
        termsTextView.setText(Html.fromHtml(getString(R.string.terms_and_conditions), Html.FROM_HTML_MODE_COMPACT));

        // Initially disable the back button
        backButton.setEnabled(false);

        // Scroll listener to check if the user has scrolled to the bottom
        termsScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            // Check if the ScrollView is scrolled to the bottom
            View contentView = termsScrollView.getChildAt(0);
            if (contentView != null &&
                    contentView.getBottom() <= (termsScrollView.getScrollY() + termsScrollView.getHeight())) {
                if (!hasScrolledToBottom) {
                    hasScrolledToBottom = true; // Set the flag to true
                    backButton.setEnabled(true); // Enable the back button
                }
            }
        });

        // Back button listener to finish the activity and send result
        backButton.setOnClickListener(v -> {
            // Return result indicating that the terms were accepted
            setResult(RESULT_OK);  // RESULT_OK means terms have been accepted
            finish();  // Close the Terms and Conditions activity
        });
    }
}