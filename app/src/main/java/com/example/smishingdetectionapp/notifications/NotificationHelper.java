package com.example.smishingdetectionapp.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;

import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import com.example.smishingdetectionapp.detections.DetectionsActivity;

import androidx.core.app.NotificationCompat;

import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;

public class NotificationHelper {

    // context object to access system resources and services
    // constructor to initialise context - context is used to access system services
    private Context mContext;

    public NotificationHelper(Context context) {
        mContext = context;
    }

    // method to create and display a notification
    // parameters include NotificationType Object which includes different attributes for different types of notifications.
    // different attributes for different types of notifications which changes how the createNotification method creates/displays the notifications
    public void createNotification(NotificationType type, String title, String message)
    {
        // check whether or not the notification type is enabled or disabled
        if(!type.getEnabled())
        {
            // if disabled, do not create a notification
            Log.i("NotificationHelper", type.getChannelName() + " is disabled");
            return;
        }

        // generate a unique ID for the created notification - this is so that notifications arent overwritten by new notifications
        int notificationID = generateNotificationID();
        // get the system's NotificationManager service to manage notifications
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // create notification channel if it doesn't exist
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(type, notificationManager);
        }

        // notification builder - uses provided parameters and context to build notification content
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, type.getChannelID())
                .setSmallIcon(R.drawable.hardhat_logo_notif)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(type.getImportance())
                .setAutoCancel(true);

        // if the message is longer than 100 characters, use the expanded style notification
        if (message.length() > 100) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }

        if (type.getChannelID().equals(mContext.getString(R.string.detection_channel_id)))
        {
            // this intent opens the detections activity upon action
            Intent intent = new Intent(mContext, DetectionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            builder.setContentIntent(pendingIntent);
        }

        if (type.getChannelID().equals(mContext.getString(R.string.push_channel_id)))
        {
            // this intent opens the update activity upon action
            Intent intent = new Intent(mContext, NewsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            builder.setContentIntent(pendingIntent);
        }

        // post the notification
        notificationManager.notify(notificationID, builder.build());
    }

    // method to create a notification channel if it doesn't already exist
    private void createNotificationChannel(NotificationType type, NotificationManager notificationManager)
    {
        // check if the channel already exists to avoid re-creating the channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(type.getChannelID()) == null) {
                // create new NotificationChannel object with the given parameters
                // from the NotificationType object
                @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(
                        type.getChannelID(),
                        type.getChannelName(),
                        type.getImportance()
                );

                // sound implementation
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                channel.setSound(soundUri, audioAttributes);

                //vibration implemention
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200});


                // set description of channel and register the channel with the system
                channel.setDescription(type.getChannelDesc());
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // generate a unique notification ID based on the current time in milliseconds
    private int generateNotificationID()
    {
        return (int) System.currentTimeMillis();
    }

}

