package com.example.smishingdetectionapp.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.content.Intent;
import android.app.PendingIntent;

import com.example.smishingdetectionapp.R;

public class SmishingWidgetLarge extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences("SmishingPrefs", Context.MODE_PRIVATE);

        int detectionCount = prefs.getInt("detectionCount", 0);
        String profileType = prefs.getString("profileLabel", "New User");
        String trend = prefs.getString("weeklyTrend", "—");
        int confidence = prefs.getInt("confidence", 0);
        int safeDays = prefs.getInt("safeDays", 0);

        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_large);

            views.setTextViewText(R.id.widget_app_title, "Smishing Detection App");
            views.setTextViewText(R.id.widget_risk_score, "Detections: " + detectionCount);
            views.setTextViewText(R.id.widget_profile_type, "User Type: " + profileType);
            views.setTextViewText(R.id.widget_streak, "Safe Days: " + safeDays);

            // Tap-to-refresh functionality
            Intent refreshIntent = new Intent(context, SmishingWidgetLarge.class);
            refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent piRefresh = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_app_title, piRefresh);

            manager.updateAppWidget(id, views);
        }
    }
}
