package com.example.smishingdetectionapp;
import android.content.Context;
import android.content.SharedPreferences;
public class PreferencesUtil {
    private static final String PREF_NAME = "AppPrefs";
    private static final String KEY_TEXT_SCALE = "text_scale";

    public static float getTextScale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getFloat(KEY_TEXT_SCALE, 1.0f); // default = 1.0f
    }

    public static void setTextScale(Context context, float scale) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putFloat(KEY_TEXT_SCALE, scale).apply();
    }
}

