package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SimilarDetectionsActivity extends AppCompatActivity {

    private TextView similarMessagesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_detections);

        similarMessagesTextView = findViewById(R.id.similarMessagesTextView);

        ArrayList<String> similarMessages = getIntent().getStringArrayListExtra("similarMessages");

        if (similarMessages != null && !similarMessages.isEmpty()) {
            displaySimilarMessages(similarMessages);
        } else {
            similarMessagesTextView.setText("No similar messages found.");
        }
    }

    private void displaySimilarMessages(ArrayList<String> messages) {
        StringBuilder displayText = new StringBuilder();
        for (String message : messages) {
            displayText.append("• ").append(message).append("\n\n");
        }
        similarMessagesTextView.setText(displayText.toString().trim());
    }
}
