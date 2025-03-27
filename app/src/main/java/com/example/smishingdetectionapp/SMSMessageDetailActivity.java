package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.sms.model.SMSMessage;

public class SMSMessageDetailActivity extends SharedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sms_message_detail);

        // Adjusting system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button setup
        ImageButton back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(v -> finish());

        // TextViews for displaying the message details
        TextView text_sender = findViewById(R.id.text_sender);
        TextView text_body = findViewById(R.id.text_body);

        // Try to retrieve the SMSMessage object from the Intent (if available)
        SMSMessage message = getIntent().getParcelableExtra("SMS_MESSAGE");

        if (message != null) {
            // If SMSMessage is passed as Parcelable, use it
            text_sender.setText(message.getSender());
            text_body.setText(message.getBody());
        } else {
            // Otherwise, retrieve individual sender and body from Intent extras
            String sender = getIntent().getStringExtra("SMS_SENDER");
            String body = getIntent().getStringExtra("SMS_BODY");

            // Set the sender and body if they are available
            if (sender != null && body != null) {
                text_sender.setText(sender);
                text_body.setText(body);
            }
        }
    }
}
