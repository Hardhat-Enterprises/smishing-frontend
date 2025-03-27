
package com.example.smishingdetectionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Back button to go back to settings dashboard
        ImageButton report_back = findViewById(R.id.report_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });


        // contact Us
        RelativeLayout rv2 = findViewById(R.id.rv_2);
        rv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:+1234567890")); // Replace with your phone number
                startActivity(phoneIntent);
            }
        });

        // Mail Us
        RelativeLayout rv1 = findViewById(R.id.rv_1);
        rv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:support@example.com")); // Replace with your email
                startActivity(emailIntent);
            }
        });

        // FAQ
        RelativeLayout rv3 = findViewById(R.id.rv_3);
        rv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Toast.makeText(HelpActivity.this, "Faq", Toast.LENGTH_SHORT).show();
            }
        });

        //Feedback
        RelativeLayout rv4 = findViewById(R.id.rv_4);
        rv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Toast.makeText(HelpActivity.this, "Feedback", Toast.LENGTH_SHORT).show();
            }
        });



    }
}