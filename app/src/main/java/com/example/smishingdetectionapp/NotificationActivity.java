package com.example.smishingdetectionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.notifications.NotificationType;

public class NotificationActivity extends SharedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the layout defined in 'activity_notification.xml'
        setContentView(R.layout.activity_notification);
        // Enable edge-to-edge display to allow your layout to extend into the window insets
        EdgeToEdge.enable(this);

        // create instances of each NotificationType for the switch
        NotificationType smishDetectionAlert = NotificationType.createSmishDetectionAlert(getApplicationContext());
        NotificationType spamDetectionAlert = NotificationType.createSpamDetectionAlert(getApplicationContext());
        NotificationType newsAlerts = NotificationType.createNewsAlert(getApplicationContext());
        NotificationType incidentAlerts = NotificationType.createIncidentAlert(getApplicationContext());
        NotificationType updateNotification = NotificationType.createUpdateNotification(getApplicationContext());
        NotificationType backupNotification = NotificationType.createBackupNotification(getApplicationContext());
        NotificationType passwordNotification = NotificationType.createPasswordNotification(getApplicationContext());

        // Initialize the Switch objects from the layout
        setupSwitch(findViewById(R.id.smishing_notification_switch), smishDetectionAlert);
        setupSwitch(findViewById(R.id.spam_notification_switch), spamDetectionAlert);
        setupSwitch(findViewById(R.id.news_notification_switch), newsAlerts);
        setupSwitch(findViewById(R.id.incident_report_switch), incidentAlerts);
        setupSwitch(findViewById(R.id.update_notification_switch), updateNotification);
        setupSwitch(findViewById(R.id.backup_reminder_switch), backupNotification);
        setupSwitch(findViewById(R.id.password_security_check_switch), passwordNotification);



        // Additional UI setup
        // Initialize the main view container object from the layout
        View mainView = findViewById(R.id.notification_main);
        // Check if the main view is not null to safely apply window insets
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                // Retrieve insets for system bars and apply them as padding to the main view
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                // Consume the system window insets so they are not applied elsewhere
                return insets.consumeSystemWindowInsets();
            });
        } else {
            // Log an error if the main view is not found
            Log.e("NotificationActivity", "mainView is null");
        }

        // Setup for the back button
        ImageButton notification_back = findViewById(R.id.notification_back);
        // Check if the back button is initialized properly
        if (notification_back != null) {
            // Set an onClick listener to handle the back button's behavior
            notification_back.setOnClickListener(v -> {
                // Start SettingsActivity when back button is pressed
                startActivity(new Intent(this, SettingsActivity.class));
                // Close the current activity
                finish();
            });
        } else {
            // Log an error if the back button is null
            Log.e("NotificationActivity", "Back button is null");
        }

        // Setup for button that takes you to notification settings in your device
        Button settingsButton = findViewById(R.id.open_notification_settings_button);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
            }
            startActivity(intent);

        });

    }

    // method to setup switch with a notificationType object.
    private void setupSwitch(Switch switchButton, NotificationType notificationType) {
        // if the switch's value isnt null
        if (switchButton != null){
            // set switchButton value to the notificationType's isEnabled value
            switchButton.setChecked(notificationType.getEnabled());
            // when switch change listener is activated (user switch input)
            // change switch's isChecked value and change the notificationType's isEnabled value
            switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                notificationType.setEnabled(isChecked);
            });
        }
        else {
            Log.e("NotificationActivity","Switch button is Null");
        }
    }
}




