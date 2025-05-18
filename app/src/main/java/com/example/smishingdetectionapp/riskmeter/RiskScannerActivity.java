package com.example.smishingdetectionapp.riskmeter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.smishingdetectionapp.riskmeter.PulseInjectorKt.injectPulsing;

public class RiskScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riskscanner);

        ComposeView pulseView = findViewById(R.id.pulseComposeView);
        injectPulsing(pulseView);

        TextView scanningText = findViewById(R.id.scanningText);

        scanningText.setVisibility(View.VISIBLE);
        pulseView.setVisibility(View.VISIBLE);


        boolean disableSmsRisk = getIntent().getBooleanExtra("DISABLE_SMS_RISK", false);
        boolean disableAgeRisk = getIntent().getBooleanExtra("DISABLE_AGE_RISK", false);
        boolean disableSecurityRisk = getIntent().getBooleanExtra("DISABLE_SECURITY_RISK", false);


        new Handler().postDelayed(() -> {
            Intent intent = new Intent(RiskScannerActivity.this, RiskResultActivity.class);
            intent.putExtra("DISABLE_SMS_RISK", disableSmsRisk);
            intent.putExtra("DISABLE_AGE_RISK", disableAgeRisk);
            intent.putExtra("DISABLE_SECURITY_RISK", disableSecurityRisk);
            startActivity(intent);
            finish();
        }, 9000);

        // navigation bar
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_report) {
                Intent i = new Intent(this, CommunityReportActivity.class);
                i.putExtra("source", "home");
                startActivity(i);
                overridePendingTransition(0,0);
                finish();
                return true;

            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        // back button
        ImageButton report_back = findViewById(R.id.RiskScanner_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, RiskScannerTCActivity.class));
            finish();
        });
    }
}
