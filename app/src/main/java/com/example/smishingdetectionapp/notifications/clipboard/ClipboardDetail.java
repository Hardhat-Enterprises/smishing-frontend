package com.example.smishingdetectionapp.notifications.clipboard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.R;

public class ClipboardDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard_detail);

        TextView fullTextView = findViewById(R.id.fullTextView);
        TextView detectedAtView = findViewById(R.id.detectedAtView);

        String fullText = getIntent().getStringExtra("fullText");
        String scannedAt = getIntent().getStringExtra("scannedAt");

        fullTextView.setText(fullText);
        detectedAtView.setText("Detected On: " + scannedAt);
    }
}