package com.example.smishingdetectionapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.sms.SMSAdapter;
import com.example.smishingdetectionapp.sms.SMSClickListener;
import com.example.smishingdetectionapp.sms.model.SMSMessage;
import com.example.smishingdetectionapp.sms.SMSExtractor;

import java.util.ArrayList;

public class SmsActivity extends SharedActivity implements SMSClickListener {
    private ArrayList<SMSMessage> smsMessageList = new ArrayList<>();
    private static final int READ_SMS_PERMISSION_CODE = 1;

    RecyclerView smsRecyclerView; // Holds the recyclerview which displays the sms messages list
    SMSAdapter smsAdapter;
    TextView noSMSMessagesText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sms);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(v -> {
            finish();
        });

        noSMSMessagesText = findViewById(R.id.no_messages_text);
        smsRecyclerView = findViewById(R.id.messages_recycler_view);
        smsRecyclerView.setHasFixedSize(true); // Set to improve performance since changes in content do not change layout size
        smsRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager for RecyclerView
        smsAdapter = new SMSAdapter(this); // Initialize the adapter with context
        smsRecyclerView.setAdapter(smsAdapter);
        SMSExtractor smsExtractor = new SMSExtractor(getApplicationContext());

        //checking the sms read permission on runtime
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            //requesting permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_CODE);
        } else {
            smsAdapter.updateMessagesList(smsExtractor.extractAllMessages());
        }
    }

    /**
     * get all the inbox messages
     */
    private void getInboxMessages() {
        ContentResolver contentResolver = getContentResolver();
        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor cursor = contentResolver.query(
                inboxUri,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                SMSMessage smsMessage = new SMSMessage(sender, body);//create Message object
                smsMessageList.add(smsMessage); // add to the list
                smsAdapter.updateMessagesList(smsMessageList);
            } while (cursor.moveToNext());
            noSMSMessagesText.setVisibility(View.GONE);
        } else {
            noSMSMessagesText.setVisibility(View.VISIBLE);
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void OnMessageClicked(com.example.smishingdetectionapp.sms.model.SMSMessage message) {
        Intent intent = new Intent(SmsActivity.this, SMSMessageDetailActivity.class);
        intent.putExtra("SMS_MESSAGE", message);
        startActivity(intent);
    }

    /**
     * permission receiver
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getInboxMessages();
            }
        }
    }
}