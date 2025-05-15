package com.example.smishingdetectionapp.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.R;

public class SafeLinkCheckerActivity extends AppCompatActivity {

    private EditText urlInput;
    private Button checkButton;
    private TextView resultBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_link_checker);

        // Enable action bar back button (optional if ActionBar exists)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Safe Link Checker");
        }

        // Custom back button from layout (optional if not using action bar)
        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        urlInput = findViewById(R.id.editTextUrl);
        checkButton = findViewById(R.id.buttonCheckLink);
        resultBox = findViewById(R.id.textResult);

        checkButton.setOnClickListener(v -> {
            String url = urlInput.getText().toString().trim();
            if (url.isEmpty()) {
                resultBox.setText("\u26A0\uFE0F Please enter a URL.");
            } else if (!isValidUrl(url)) {
                resultBox.setText("\u274C Invalid URL format.");
            } else {
                // Local risk check
                String localResult = assessRisk(url);
                resultBox.setText("\uD83D\uDD0D Local Risk Assessment: " + localResult);

                // Simulated backend check
                simulateBackendCheck(url);
            }
        });
    }

    private boolean isValidUrl(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    private String assessRisk(String url) {
        int score = 0;
        String lowered = url.toLowerCase();

        // 1. Keyword detection
        String[] suspiciousKeywords = {"login", "verify", "account", "secure", "update", "free", "win", "urgent"};
        for (String keyword : suspiciousKeywords) {
            if (lowered.contains(keyword)) score += 2;
        }

        // 2. Shortened links
        if (lowered.contains("bit.ly") || lowered.contains("tinyurl") || lowered.contains("t.co")) {
            score += 2;
        }

        // 3. IP address usage
        if (lowered.matches("http[s]?://(\\d{1,3}\\.){3}\\d{1,3}.*")) {
            score += 3;
        }

        // 4. Risky domain endings
        String[] riskyTLDs = {".tk", ".ml", ".ga", ".cf", ".gq", ".xyz", ".top"};
        for (String tld : riskyTLDs) {
            if (lowered.endsWith(tld)) score += 2;
        }

        // 5. Unsecured HTTP
        if (lowered.startsWith("http://")) score += 1;

        // 6. Lengthy query strings
        if (lowered.contains("?") && lowered.split("\\?")[1].length() > 50) {
            score += 1;
        }

        if (score >= 7) return "\uD83D\uDEA8 High Risk: Strong phishing patterns found.";
        else if (score >= 4) return "\u26A0\uFE0F Moderate Risk: Several suspicious signs.";
        else return "\u2705 Low Risk: No major threats detected.";
    }

    private void simulateBackendCheck(String url) {
        resultBox.append("\n\n\uD83D\uDD04 Checking with external threat sources...");

        new Handler().postDelayed(() -> {
            boolean isSuspicious = url.contains("free") || url.contains("win") || url.contains("login");
            String backendResult = isSuspicious ? "Suspicious" : "Safe";
            String reason = isSuspicious ?
                    "Pattern matches known phishing tactics" :
                    "No risky indicators found in external sources";

            resultBox.append("\n\n\u2705 Backend Result: " + backendResult +
                    "\n\uD83D\uDCCC Reason: " + reason);
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

