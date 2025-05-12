package com.example.smishingdetectionapp.ui;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.ReportingActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmishingWidgetMedium extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_medium);

            // Simulated data
            views.setTextViewText(R.id.widget_medium_count, "7");

            @SuppressLint("SimpleDateFormat")
            String timeStamp = new SimpleDateFormat("hh:mm a").format(new Date());
            views.setTextViewText(R.id.widget_medium_timestamp, "Updated: " + timeStamp);

            // Open App Intent
            Intent openIntent = new Intent(context, MainActivity.class);
            PendingIntent piOpen = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_open_app_btn, piOpen);

            // Report Smishing Intent
            Intent reportIntent = new Intent(context, ReportingActivity.class);
            PendingIntent piReport = PendingIntent.getActivity(context, 1, reportIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_report_btn, piReport);

            manager.updateAppWidget(id, views);
        }
    }
}
