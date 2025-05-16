package com.example.smishingdetectionapp.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.content.Intent;
import android.app.PendingIntent;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmishingWidgetLarge extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences("SmishingPrefs", Context.MODE_PRIVATE);

        int detectionCount = prefs.getInt("detectionCount", 0);
        String profileType = prefs.getString("profileLabel", "New User");
        int safeDays = prefs.getInt("safeDays", 0);

        String timestamp = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
        prefs.edit().putString("lastUpdated", timestamp).apply();

        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_large);

            views.setTextViewText(R.id.widget_app_title, "Smishing Detection App");
            views.setTextViewText(R.id.widget_risk_score, "Detections: " + detectionCount);
            views.setTextViewText(R.id.widget_profile_type, "User Type: " + profileType);
            views.setTextViewText(R.id.widget_streak, "Safe Days: " + safeDays);
            views.setTextViewText(R.id.widget_large_timestamp, "Updated: " + timestamp);

            // Update
            manager.updateAppWidget(id, views);
        }
    }
}
