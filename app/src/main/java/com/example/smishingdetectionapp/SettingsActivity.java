package com.example.smishingdetectionapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.smishingdetectionapp.Community.CommunityHomeActivity;
import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.chat.ChatAssistantActivity;
import com.example.smishingdetectionapp.ui.account.AccountActivity;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {
    private static final int TIMEOUT_MILLIS = 10000;
    private boolean isAuthenticated = false;
    private BiometricPrompt biometricPrompt;

    private SeekBar seekBarFontScale;
    private TextView textScaleLabel;
    private float textScale;
    private SharedPreferences prefs;
    private ScrollView scrollView;

    private Dialog dialog;
    private Button dialogCancel, dialogSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isBold = prefs.getBoolean("bold_text_enabled", false);
        setTheme(isBold ? R.style.Theme_SmishingDetectionApp_Bold : R.style.Theme_SmishingDetectionApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        scrollView = findViewById(R.id.settingsScroll);

        SwitchCompat darkModeSwitch = findViewById(R.id.dark_mode_switch);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDarkMode);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            recreate();
        });

        seekBarFontScale = findViewById(R.id.seekBarFontScale);
        textScaleLabel = findViewById(R.id.textScaleLabel);
        textScale = PreferencesUtil.getTextScale(this);
        seekBarFontScale.setProgress((int) (textScale * 100));
        updateScaleLabel();

        seekBarFontScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float newScale = progress / 100f;
                newScale = Math.max(0.8f, Math.min(newScale, 1.5f));
                textScale = newScale;
                PreferencesUtil.setTextScale(SettingsActivity.this, textScale);
                updateScaleLabel();
                applyFontScale();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        if (isBold) applyBoldToAllWidgets(scrollView);

        SwitchCompat boldSwitch = findViewById(R.id.bold_text);
        if (boldSwitch != null) {
            boldSwitch.setChecked(isBold);
            boldSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                saveScrollPosition();
                prefs.edit().putBoolean("bold_text_enabled", isChecked).apply();
                recreate();
            });
        }

        boolean isFromNav = getIntent().getBooleanExtra("from_navigation", false);
        boolean isCold = prefs.getBoolean("cold_start", true);

        if (isFromNav || isCold) {
            scrollView.post(() -> scrollView.scrollTo(0, 0));
            prefs.edit().putBoolean("cold_start", false).apply();
        } else {
            restoreScrollPosition();
        }

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_settings);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_report) {
                Intent i = new Intent(this, CommunityReportActivity.class);
                i.putExtra("source", "home");
                startActivity(i);
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(this, NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("from_navigation", true);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        findViewById(R.id.accountBtn).setOnClickListener(v -> triggerBiometricAuthenticationWithTimeout());
        findViewById(R.id.imageView7).setOnClickListener(v -> startActivity(new Intent(this, SmishingRulesActivity.class)));
        findViewById(R.id.reportBtn).setOnClickListener(v -> startActivity(new Intent(this, ReportingActivity.class)));
        findViewById(R.id.helpBtn).setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));
        findViewById(R.id.aboutMeBtn).setOnClickListener(v -> startActivity(new Intent(this, AboutMeActivity.class)));
        findViewById(R.id.aboutUsBtn).setOnClickListener(v -> startActivity(new Intent(this, AboutUsActivity.class)));
        findViewById(R.id.chatAssistantBtn).setOnClickListener(v -> startActivity(new Intent(this, ChatAssistantActivity.class)));
        findViewById(R.id.feedbackBtn).setOnClickListener(v -> startActivity(new Intent(this, FeedbackActivity.class)));
        findViewById(R.id.communityBtn).setOnClickListener(v -> {
            Intent i = new Intent(this, CommunityHomeActivity.class);
            i.putExtra("source", "settings");
            startActivity(i);
        });
        findViewById(R.id.guardianBtn).setOnClickListener(v -> startActivity(new Intent(this, GuardianActivity.class)));

        Button signoutBtn = findViewById(R.id.buttonSignOut);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        dialog = new Dialog(SettingsActivity.this);
        dialog.setContentView(R.layout.dialog_signout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogCancel = dialog.findViewById(R.id.signoutCancelBtn);
        dialogSignout = dialog.findViewById(R.id.signoutBtn);

        dialogCancel.setOnClickListener(v -> dialog.dismiss());
        dialogSignout.setOnClickListener(v -> {
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        });
        signoutBtn.setOnClickListener(v -> dialog.show());

        if (isTaskRoot()) {
            prefs.edit().putBoolean("cold_start", true).apply();
            prefs.edit().remove("scroll_pos").apply();
        }
    }

    private void triggerBiometricAuthenticationWithTimeout() {
        int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL;
        BiometricManager bm = BiometricManager.from(this);
        switch (bm.canAuthenticate(authenticators)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                biometricPrompt = getPrompt();
                biometricPrompt.authenticate(buildPromptInfo(authenticators));
                startTimeoutTimer();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                openAccountActivity();
                break;
            default:
                notifyUser("Biometric authentication unavailable");
                openAccountActivity();
                break;
        }
    }

    private BiometricPrompt.PromptInfo buildPromptInfo(int authenticators) {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication Required")
                .setDescription("Please authenticate to access your account settings")
                .setAllowedAuthenticators(authenticators)
                .build();
    }

    private BiometricPrompt getPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        return new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                notifyUser("Authentication Error: " + errString);
                redirectToSettingsActivity();
            }
            @Override public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                notifyUser("Authentication Succeeded!");
                isAuthenticated = true;
                openAccountActivity();
            }
            @Override public void onAuthenticationFailed() {
                notifyUser("Authentication Failed");
            }
        });
    }

    private void startTimeoutTimer() {
        new Handler().postDelayed(() -> {
            if (!isAuthenticated) {
                notifyUser("Authentication timed out. Redirecting to Settings...");
                biometricPrompt.cancelAuthentication();
                redirectToSettingsActivity();
            }
        }, TIMEOUT_MILLIS);
    }

    private void redirectToSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
        finish();
    }

    private void openAccountActivity() {
        startActivity(new Intent(this, AccountActivity.class));
        finish();
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateScaleLabel() {
        int percentage = (int) (textScale * 100);
        textScaleLabel.setText(percentage + "%");
    }

    private void applyFontScale() {
        Configuration configuration = new Configuration(getResources().getConfiguration());
        configuration.fontScale = textScale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        recreate();
    }

    private void saveScrollPosition() {
        if (scrollView != null) {
            int scrollY = scrollView.getScrollY();
            prefs.edit().putInt("scroll_pos", scrollY).apply();
        }
    }

    private void restoreScrollPosition() {
        int savedScrollY = prefs.getInt("scroll_pos", 0);
        final int scrollY = isTaskRoot() ? 0 : savedScrollY;
        scrollView.post(() -> scrollView.scrollTo(0, scrollY));
    }

    private void applyBoldToAllWidgets(View root) {
        if (!(root instanceof ViewGroup)) return;
        ViewGroup group = (ViewGroup) root;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof TextView) ((TextView) child).setTypeface(null, Typeface.BOLD);
            applyBoldToAllWidgets(child);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveScrollPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!prefs.getBoolean("cold_start", false)) {
            restoreScrollPosition();
        }
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        finish();
        super.onBackPressed();
    }
}
