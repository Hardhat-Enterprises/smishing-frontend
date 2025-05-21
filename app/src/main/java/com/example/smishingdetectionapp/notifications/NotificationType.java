package com.example.smishingdetectionapp.notifications;

import static com.example.smishingdetectionapp.R.string.*;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

public class NotificationType {

    private final Context context; // application context
    private final String channelID; // channel ID for the notification channel

    private final String channelName; // name of the notification channel
    private final String channelDesc; // description of the notification channel
    private final String key; // key for the notificationType for saving enabled preference
    private boolean isEnabled; // to determine and set whether the notification type is enabled/disabled
    private static int importance; // importance level for the notification channel - determines how intrusive notifications are

    private static final String PREFS_FILE = "notification_settings";

    // NotificationType object constructor
    public NotificationType(Context context, int keyString, int channelIDString, int channelNameString, int channelDescString, int importance)
    {
        //sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        this.context = context;
        this.key = context.getString(keyString);;
        this.channelID = context.getString(channelIDString);
        this.channelName = context.getString(channelNameString);
        this.channelDesc = context.getString(channelDescString);
        this.importance = importance;
        this.isEnabled = loadEnabledState();
    }

    //
    public String getChannelID()
    {
        return channelID;
    } // returns channel ID
    public String getChannelName()
    {
        return channelName;
    } // returns channel name
    public String getChannelDesc()
    {
        return channelDesc;
    } // returns channel description
    public int getImportance()
    {
        return importance;
    } // returns channel importance/priority
    public boolean getEnabled() { return this.isEnabled; } // returns channel toggle set (enable/disable)

    // sets given boolean value to notificationType's "isEnabled" value
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        saveEnabledState();
    }

    private boolean loadEnabledState() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return prefs.getBoolean(this.key, true);
    }

    // saves "isEnabled" value of the notificationType to Shared Preferences
    private void saveEnabledState() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(this.key, this.isEnabled);
        editor.apply();
    }

    // factory method for SmishDetectionAlert notification type
    public static NotificationType createSmishDetectionAlert(Context context) {
        return new NotificationType(context, smish_pref_key, detection_channel_id, detection_channel_name, detection_channel_desc, NotificationManager.IMPORTANCE_HIGH);
    }
    // factory method for SpamDetectionAlert notification type
    public static NotificationType createSpamDetectionAlert(Context context) {
        return new NotificationType(context, spam_pref_key, detection_channel_id, detection_channel_name, detection_channel_desc, NotificationManager.IMPORTANCE_HIGH);
    }
    // factory method for NewsAlert notification type
    public static NotificationType createNewsAlert(Context context) {
        return new NotificationType(context, news_pref_key, push_channel_id, push_channel_name, push_channel_desc, NotificationManager.IMPORTANCE_DEFAULT);
    }
    // factory method for IncidentAlert notification type
    public static NotificationType createIncidentAlert(Context context) {
        return new NotificationType(context, incident_pref_key, push_channel_id, push_channel_name, push_channel_desc, NotificationManager.IMPORTANCE_DEFAULT);
    }
    // factory method for UpdateNotification notification type
    public static NotificationType createUpdateNotification(Context context) {
        return new NotificationType(context, update_pref_key, system_channel_id, system_channel_name, system_channel_desc, NotificationManager.IMPORTANCE_DEFAULT);
    }
    // factory method for BackupNotification notification type
    public static NotificationType createBackupNotification(Context context) {
        return new NotificationType(context, backup_pref_key, system_channel_id, system_channel_name, system_channel_desc, NotificationManager.IMPORTANCE_DEFAULT);
    }
    // factory method for PasswordNotification notification type
    public static NotificationType createPasswordNotification(Context context) {
        return new NotificationType(context, password_pref_key, system_channel_id, system_channel_name, system_channel_desc, NotificationManager.IMPORTANCE_DEFAULT);
    }

}

