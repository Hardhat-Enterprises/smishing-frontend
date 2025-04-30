package com.example.smishingdetectionapp.riskmeter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.AlignmentSpan;
import android.text.style.StyleSpan;
import android.text.Layout;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class RiskResultActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView percentageText;
    TextView riskLevelText;

    //these are the circle indicators next to each digital habit that will change colour based on risk level
    View lightAgeGroup, lightSmsApp, lightSecurityApp, lightSpamFilter, lightDeviceLock, lightUnknownSources, lightSmsBehaviour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_result);

        progressBar = findViewById(R.id.circularProgressBar);
        percentageText = findViewById(R.id.percentageText);
        riskLevelText = findViewById(R.id.riskLevelText);

        lightAgeGroup = findViewById(R.id.light_age_group);
        lightSmsApp = findViewById(R.id.light_sms_app);
        lightSecurityApp = findViewById(R.id.light_security_app);
        lightSpamFilter = findViewById(R.id.light_spam_filter);
        lightDeviceLock = findViewById(R.id.light_device_lock);
        lightUnknownSources = findViewById(R.id.light_unknown_sources);
        lightSmsBehaviour = findViewById(R.id.light_sms_behaviour);

        boolean disableSmsRisk = getIntent().getBooleanExtra("DISABLE_SMS_RISK", false);
        boolean disableAgeRisk = getIntent().getBooleanExtra("DISABLE_AGE_RISK", false);
        boolean disableSecurityRisk = getIntent().getBooleanExtra("DISABLE_SECURITY_RISK", false);


        //our logic scan and updates to progress bar, texts and lights
        RiskScannerLogic.scanHabits(this, progressBar, riskLevelText,
                lightAgeGroup, lightSmsApp, lightSecurityApp, lightSpamFilter,
                lightDeviceLock, lightUnknownSources, lightSmsBehaviour, disableSmsRisk,
                disableAgeRisk, disableSecurityRisk);

        // this is for view anlysis text to make it a clickable text view
        TextView viewAnalysisText = findViewById(R.id.textViewRiskDetails);
        viewAnalysisText.setOnClickListener(v -> {
            int score = RiskScannerLogic.getCalculatedScore();
            showRiskAnalysisDialog(score);
        });

        //bottom naviagation bar
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_news);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });

        // this is the back button to go to home page
        ImageButton report_back = findViewById(R.id.RiskScannerResult_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    // this is the animation for the progress bar to make the percentage and fill move
    public void animateProgress(ProgressBar progressBar, TextView percentageText, int score) {
        progressBar.setMax(100);

        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, score);
        animator.setDuration(1500);
        animator.start();

        ValueAnimator textAnimator = ValueAnimator.ofInt(0, score);
        textAnimator.setDuration(1500);
        textAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            percentageText.setText(animatedValue + "%");
        });
        textAnimator.start();
    }

    // this is most of the java for when you click the view risk analysis and then get the bottom sheet dialog
    public void showRiskAnalysisDialog(int score) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.dialog_risk_analysis, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView riskTitle = bottomSheetView.findViewById(R.id.textRiskTitle);
        TextView riskMessage = bottomSheetView.findViewById(R.id.textRiskMessage);
        Button closeBtn = bottomSheetView.findViewById(R.id.btnCloseDialog);

        List<String> risks = RiskScannerLogic.getTriggeredRisks();
        SpannableStringBuilder message = new SpannableStringBuilder();

        // the blue message saying what the risk level is
        String header;
        if (score <= 30) {
            header = "Your digital habits suggest a Low Risk of being susceptible to smishing attacks.\n\n";
        } else if (score <= 60) {
            header = "Your digital habits suggest a Moderate Risk of being susceptible to smishing attacks.\n\n";
        } else {
            header = "Your digital habits suggest a High Risk of being susceptible to smishing attacks.\n\n";
        }

        // adding styling to detected issues and the text for it
        SpannableString riskHeader = new SpannableString(header);
        riskHeader.setSpan(new StyleSpan(Typeface.BOLD), 0, riskHeader.length(), 0);
        riskHeader.setSpan(new ForegroundColorSpan(android.graphics.Color.parseColor("#4B74F0")), 0, riskHeader.length(), 0);
        riskHeader.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, riskHeader.length(), 0);
        message.append(riskHeader);

        if (!risks.isEmpty()) {
            SpannableString issuesHeader = new SpannableString("\n Detected Issues\n\n");
            issuesHeader.setSpan(new StyleSpan(Typeface.BOLD), 0, issuesHeader.length(), 0);
            message.append(issuesHeader);

            for (String risk : risks) {
                message.append("• ").append(risk).append("\n");
            }
        } else {
            message.append("\n Woohoo, no major risks detected!\n");
        }

        // adding styling to recommendations and recommendations
        SpannableString recHeader = new SpannableString("\n Recommendations\n\n");
        recHeader.setSpan(new StyleSpan(Typeface.BOLD), 0, recHeader.length(), 0);
        message.append(recHeader);
        if (score > 30) {
            message.append("• Set up a strong password or PIN to protect your data and reduce the risk of unauthorided access.\n");
            message.append("• Install antivirus or security apps to detect and block suspicious activity or attacks.\n");
            message.append("• Disable installations from unknown sources to prevent the risk of malicious apps being installed.\n");
            message.append("• Enable a spam filter to help automatically detect and block smishing messages.\n");
            message.append("• Avoid clicking on links in SMS messages from unknown senders to reduce exposure to malicious websites.\n");
            message.append("• Report suspicious SMS messages to your mobile provider to help block further threats.\n");
            message.append("• Take a short cybersecurity awareness course to learn how to identify and avoid common smishing techniques.\n");
        } else {
            message.append("• Keep up your excellent security habits! You're doing great.\n");
        }

        riskMessage.setText(message);
        closeBtn.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }
}
