package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RiskScoringActivity extends AppCompatActivity {

    private TextView riskBadge;
    private ProgressBar riskMeter;
    private TextView riskScorePercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_scoring);

        // Initialize the UI components
        riskBadge = findViewById(R.id.riskBadge);
        riskMeter = findViewById(R.id.riskMeter);
        riskScorePercentage = findViewById(R.id.riskScorePercentage);

        // Simulate receiving a message with a risk score (this should come from your AI model)
        int riskScore = getRiskScoreFromMessage();  // Example: 70% risk

        // Update UI with the risk score
        updateRiskUI(riskScore);
    }

    private int getRiskScoreFromMessage() {
        // Simulated risk score (replace with actual AI model result)
        return 70; // For example, the AI model could return 70% risk
    }

    private void updateRiskUI(int score) {
        // Update the progress bar with the score
        riskMeter.setProgress(score);

        // Update the risk score percentage text
        riskScorePercentage.setText(score + "%");

        // Update the risk badge based on the score
        if (score < 30) {
            riskBadge.setText("Risk Level: Low");
            riskBadge.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light)); // Green
        } else if (score < 70) {
            riskBadge.setText("Risk Level: Medium");
            riskBadge.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light)); // Yellow
        } else {
            riskBadge.setText("Risk Level: High");
            riskBadge.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light)); // Red
        }
    }
}
