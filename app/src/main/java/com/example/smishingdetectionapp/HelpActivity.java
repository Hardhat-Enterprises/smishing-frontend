package com.example.smishingdetectionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HelpActivity extends SharedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_updated);

        // Apply window insets for Edge-to-Edge experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button to go back to settings dashboard
        ImageButton report_back = findViewById(R.id.report_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // "Call Us" functionality
        RelativeLayout rv2 = findViewById(R.id.rv_2);
        rv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:+1234567890")); // Replace with your phone number
                startActivity(phoneIntent);
            }
        });

        // "Mail Us" functionality
        RelativeLayout rv1 = findViewById(R.id.rv_1);
        rv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:support@example.com")); // Replace with your email
                startActivity(emailIntent);
            }
        });

        // FAQ functionality (already implemented elsewhere)
        RelativeLayout rv3 = findViewById(R.id.rv_3);
        rv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, show a Toast (or launch FAQActivity if available)
                Toast.makeText(HelpActivity.this, "FAQ screen not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        // Feedback functionality: Launch FeedbackActivity
        RelativeLayout rv4 = findViewById(R.id.rv_4);
        rv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the FeedbackActivity that uses the feedback.xml layout
                startActivity(new Intent(HelpActivity.this, FeedbackActivity.class));
            }
        });
    }
}
