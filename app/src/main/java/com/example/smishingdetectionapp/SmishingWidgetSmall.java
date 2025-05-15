package com.example.smishingdetectionapp.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.example.smishingdetectionapp.R;

public class SmishingWidgetSmall extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_small);
            views.setTextViewText(R.id.widget_small_count, "3");  // Simulated count
            manager.updateAppWidget(id, views);
        }
    }
}
