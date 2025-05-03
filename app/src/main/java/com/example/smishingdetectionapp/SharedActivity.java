package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import com.example.smishingdetectionapp.ui.login.LoginActivity;


public abstract class SharedActivity extends AppCompatActivity {
    private static final int SESSION_TIMEOUT_MS = 1200000; // Default 20 minute timer for session timeout // feel free to test with a shorter time to double check
    private static final int POPUP_TIMEOUT_MS = 30000; // Default 30 second timer for popup timeout
    private Handler sessionHandler;
    private Handler popupHandler;
    private Runnable sessionTimeoutRunnable;
    private Runnable popupTimeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isBold = prefs.getBoolean("bold_text_enabled", false);
        setTheme(isBold ? R.style.Theme_SmishingDetectionApp_Bold : R.style.Theme_SmishingDetectionApp);
        super.onCreate(savedInstanceState);
        setupSessionTimeout();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("bold_text_enabled", false)) {
            View root = findViewById(android.R.id.content);
            applyBoldToAllWidgets(root);
        }
    }
    private void applyBoldToAllWidgets(View root) {
        if (!(root instanceof ViewGroup)) return;

        ViewGroup group = (ViewGroup) root;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);

            if (child instanceof Switch || child instanceof androidx.appcompat.widget.SwitchCompat) {
                ((TextView) child).setTypeface(null, Typeface.BOLD);
            }

            if (child instanceof Button || child instanceof com.google.android.material.button.MaterialButton) {
                ((TextView) child).setTypeface(null, Typeface.BOLD);
            }

            if (child instanceof ViewGroup) {
                applyBoldToAllWidgets(child);
            }
        }
    }



    private void setupSessionTimeout() {
        sessionHandler = new Handler();
        sessionTimeoutRunnable = this::onSessionTimeout;
        popupHandler = new Handler();
        popupTimeoutRunnable = this::onPopupTimeout;
        resetSessionTimeout();
    }

    private void resetSessionTimeout() {
        if (sessionHandler != null) {
            sessionHandler.removeCallbacks(sessionTimeoutRunnable);
            sessionHandler.postDelayed(sessionTimeoutRunnable, SESSION_TIMEOUT_MS);
        }
    }

    private void onSessionTimeout() {
        if (!isFinishing() && shouldShowSessionTimeoutPopup()) {
            showSessionTimeoutPopup();
        }
    }

    protected boolean shouldShowSessionTimeoutPopup() {
        return true; // Makes it the default to always show the session timeout popup
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetSessionTimeout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sessionHandler != null) {
            sessionHandler.removeCallbacks(sessionTimeoutRunnable);
        }
        if (popupHandler != null) {
            popupHandler.removeCallbacks(popupTimeoutRunnable);
        }
    }

    private void showSessionTimeoutPopup() {
        PopupSessionTimeout popup = new PopupSessionTimeout();
        popup.setSessionTimeoutListener(this::resetSessionTimeout);
        popup.show(getSupportFragmentManager(), "SessionTimeoutPopup");

        // New timer for the popup timeout (10 seconds)
        popupHandler.removeCallbacks(popupTimeoutRunnable);
        popupHandler.postDelayed(popupTimeoutRunnable, POPUP_TIMEOUT_MS);
    }

    private void onPopupTimeout() {
        if (!isFinishing()) {
            logout();
        }
    }


    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}