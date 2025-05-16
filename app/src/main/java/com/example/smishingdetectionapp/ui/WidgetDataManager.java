package com.example.smishingdetectionapp.ui;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WidgetDataManager {

    private static final String PREF_NAME = "SmishingPrefs";

    public static void updateDetectionCount(Context context, int totalDetections) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("detectionCount", totalDetections);

        if (totalDetections >= 10) {
            editor.putString("profileLabel", "Vigilant User");
        } else if (totalDetections >= 5) {
            editor.putString("profileLabel", "Cautious User");
        } else {
            editor.putString("profileLabel", "New User");
        }

        editor.apply();
    }

    public static void updateSafeDayStreak(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastDay = prefs.getString("lastDay", "");

        if (!today.equals(lastDay)) {
            int streak = prefs.getInt("safeDays", 0) + 1;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("safeDays", streak);
            editor.putString("lastDay", today);
            editor.apply();
        }
    }
}
