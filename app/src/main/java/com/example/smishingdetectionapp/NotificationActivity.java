package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
//            // Show test notification
//            showNotification("Test Notification", "This is a test notification triggered by the settings button.");

            // Open system notification settings
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

        // Delete this button before prod
        Button testNotificationButton = findViewById(R.id.button_test_notifications);
            testNotificationButton.setOnClickListener(v -> {
                sendAllTestNotifications();
            });
        }

    private void sendAllTestNotifications() {
        Context context = this;

        sendNotification(context, NotificationType.createSmishDetectionAlert(context), "Smishing Alert", "This is a test smishing notification.");
        sendNotification(context, NotificationType.createSpamDetectionAlert(context), "Spam Alert", "This is a test spam notification.");
        sendNotification(context, NotificationType.createNewsAlert(context), "News Update", "This is a test news update.");
        sendNotification(context, NotificationType.createIncidentAlert(context), "Incident Report", "This is a test incident alert.");
        sendNotification(context, NotificationType.createUpdateNotification(context), "App Update", "This is a test update notification.");
        sendNotification(context, NotificationType.createBackupNotification(context), "Backup Complete", "This is a test backup notification.");
        sendNotification(context, NotificationType.createPasswordNotification(context), "Password Reminder", "This is a test password-related notification.");
    }

    private void sendNotification(Context context, NotificationType type, String title, String message) {
        if (!type.getEnabled()) {
            Log.d("Notification", "Notification disabled for: " + type.getChannelID());
            return;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    type.getChannelID(),
                    type.getChannelName(),
                    type.getImportance()
            );
            channel.setDescription(type.getChannelDesc());
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, type.getChannelID())
                .setSmallIcon(R.drawable.new_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(type.getImportance())
                .setAutoCancel(true); // optional: cancel on click

        // Check permission for Android 13+
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Notification permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        int notificationId = (title + type.getChannelID()).hashCode(); // unique per type
        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
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

//    private void showNotification(String title, String message) {
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        String channelId = "my_channel_id";
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    channelId,
//                    "Default Channel",
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            channel.setDescription("Used for app alerts");
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.new_logo)  // logo
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true);
//
//        notificationManager.notify(1, builder.build());
//    }
}