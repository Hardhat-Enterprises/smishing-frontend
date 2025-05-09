package com.example.smishingdetectionapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.material.card.MaterialCardView;


public class HelpActivity extends SharedActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_updated);


        // Adjust padding for system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Back button: finish activity
        ImageButton helpBack = findViewById(R.id.help_back);
        helpBack.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Menu button: handle as needed
        ImageButton helpMenu = findViewById(R.id.help_menu);
        helpMenu.setOnClickListener(v -> {
            // Implement menu actions if necessary
        });


        // Common Topics Click Listeners
        MaterialCardView cardTopic1 = findViewById(R.id.cardTopic1);
        if (cardTopic1 != null) {
            cardTopic1.setOnClickListener(v ->
                    Toast.makeText(HelpActivity.this, "How to detect a smishing message", Toast.LENGTH_SHORT).show()
            );
        }
        MaterialCardView cardTopic2 = findViewById(R.id.cardTopic2);
        if (cardTopic2 != null) {
            cardTopic2.setOnClickListener(v ->
                    Toast.makeText(HelpActivity.this, "How to report a suspicious SMS", Toast.LENGTH_SHORT).show()
            );
        }
        MaterialCardView cardTopic3 = findViewById(R.id.cardTopic3);
        if (cardTopic3 != null) {
            cardTopic3.setOnClickListener(v ->
                    Toast.makeText(HelpActivity.this, "What is smishing vs. phishing?", Toast.LENGTH_SHORT).show()
            );
        }


        // FAQ Cards Click Listeners
        MaterialCardView cardFAQ2 = findViewById(R.id.cardFAQ2);
        if (cardFAQ2 != null) {
            cardFAQ2.setOnClickListener(v -> {
                Intent intent = new Intent(HelpActivity.this, FaqActivity.class);
                intent.putExtra("faq_topic", "adjust_settings");
                startActivity(intent);
            });
        }


        // Contact Options Click Listeners
        MaterialCardView cardCallUs = findViewById(R.id.cardCallUs);
        if (cardCallUs != null) {
            cardCallUs.setOnClickListener(v -> {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:+1234567890")); // Replace with your phone number
                startActivity(phoneIntent);
            });
        }
        MaterialCardView cardMailUs = findViewById(R.id.cardMailUs);
        if (cardMailUs != null) {
            cardMailUs.setOnClickListener(v -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:support@example.com")); // Replace with your email address
                startActivity(emailIntent);
            });
        }
        MaterialCardView cardFeedback = findViewById(R.id.cardFeedback);
        if (cardFeedback != null) {
            cardFeedback.setOnClickListener(v ->
                    Toast.makeText(HelpActivity.this, "Send Feedback", Toast.LENGTH_SHORT).show()
            );
        }
    }
}
