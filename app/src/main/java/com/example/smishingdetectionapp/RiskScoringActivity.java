package com.example.smishingdetectionapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class RiskScoringActivity extends AppCompatActivity {

    private TextView riskBadge, riskScorePercentage;
    private ProgressBar riskMeter;
    private ImageButton backButton;
    private LottieAnimationView lottieView;

    private final int riskScore =  70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_scoring);

        // Link UI components
        riskBadge = findViewById(R.id.riskBadge);
        riskMeter = findViewById(R.id.riskMeter);
        riskScorePercentage = findViewById(R.id.riskScorePercentage);
        backButton = findViewById(R.id.backButton);
        lottieView = findViewById(R.id.lottieView);

        // Handle back button click
        backButton.setOnClickListener(v -> finish());

        // Animate score and UI
        animateRiskScore(riskScore);
    }

    private void animateRiskScore(int score) {
        // Animate progress bar
        ObjectAnimator progressAnim = ObjectAnimator.ofInt(riskMeter, "progress", 0, score);
        progressAnim.setDuration(1500);
        progressAnim.setInterpolator(new DecelerateInterpolator());
        progressAnim.start();

        // Animate percentage text
        animateText(score);

        // Update UI based on score
        if (score < 30) {
            riskBadge.setText("Risk Level: Low");
            riskBadge.setBackgroundResource(R.drawable.low_risk_badge);
            lottieView.setAnimation(R.raw.risk_low);
        } else if (score < 70) {
            riskBadge.setText("Risk Level: Medium");
            riskBadge.setBackgroundResource(R.drawable.medium_risk_badge);
            lottieView.setAnimation(R.raw.risk_medium);
            startPulseAnimation(riskBadge);
        } else {
            riskBadge.setText("Risk Level: High");
            riskBadge.setBackgroundResource(R.drawable.high_risk_badge);
            lottieView.setAnimation(R.raw.risk_high);
            startPulseAnimation(riskBadge);
        }

        // Bounce + fade-in badge
        riskBadge.setAlpha(0f);
        riskBadge.setScaleX(0.8f);
        riskBadge.setScaleY(0.8f);
        riskBadge.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Fade-in for risk percentage
        riskScorePercentage.setAlpha(0f);
        riskScorePercentage.animate()
                .alpha(1f)
                .setDuration(800)
                .start();
    }

    private void animateText(int score) {
        final Handler handler = new Handler();
        final int[] current = {0};

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (current[0] <= score) {
                    riskScorePercentage.setText(current[0] + "%");
                    current[0]++;
                    handler.postDelayed(this, 20);
                }
            }
        }, 20);
    }

    private void startPulseAnimation(TextView targetView) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(targetView, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(targetView, "scaleY", 1f, 1.1f, 1f);
        scaleX.setDuration(800);
        scaleY.setDuration(800);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setRepeatMode(ObjectAnimator.REVERSE);
        scaleY.setRepeatMode(ObjectAnimator.REVERSE);
        scaleX.start();
        scaleY.start();
    }
}