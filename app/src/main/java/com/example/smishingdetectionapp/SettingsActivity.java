package com.example.smishingdetectionapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.smishingdetectionapp.Community.CommunityHomeActivity;
import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.chat.ChatAssistantActivity;
import com.example.smishingdetectionapp.ui.ContactUsActivity;
import com.example.smishingdetectionapp.ui.account.AccountActivity;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {
    private SeekBar seekBarFontScale;
    private TextView textScaleLabel;
    private float textScale;
    private Dialog dialog;
    private Button dialogCancel, dialogSignout;
    private static final int TIMEOUT_MILLIS = 10000;
    private boolean isAuthenticated = false;
    private BiometricPrompt biometricPrompt;
    private static final String KEY_SCROLL_POSITION = "scroll_position";
    private int savedPosition = 0;
    private ScrollView scrollView;
    private SharedPreferences prefs;
    private Switch darkModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isBold = prefs.getBoolean("bold_text_enabled", false);
        setTheme(isBold ? R.style.Theme_SmishingDetectionApp_Bold : R.style.Theme_SmishingDetectionApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDarkMode);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            recreate();
        });

        textScaleLabel = findViewById(R.id.textScaleLabel);
        seekBarFontScale = findViewById(R.id.seekBarFontScale);
        textScale = PreferencesUtil.getTextScale(this);
        updateScaleLabel();
        seekBarFontScale.setProgress((int) (textScale * 10));
        seekBarFontScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float newScale = Math.max(0.8f, Math.min(progress / 10f, 1.5f));
                textScale = newScale;
                PreferencesUtil.setTextScale(SettingsActivity.this, textScale);
                updateScaleLabel();
                applyFontScale();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        scrollView = findViewById(R.id.settingsScroll);
        if (isBold) {
            applyBoldToAllSwitches(scrollView);
            applyBoldToAllWidgets(scrollView);
        }

        boolean isFromNav = getIntent().getBooleanExtra("from_navigation", false);
        boolean isColdStart = prefs.getBoolean("cold_start", true);
        if (isFromNav || isColdStart) {
            scrollView.post(() -> scrollView.scrollTo(0, 0));
            prefs.edit().putBoolean("cold_start", false).apply();
        } else {
            restoreScrollPosition();
        }

        Switch boldSwitch = findViewById(R.id.bold_text);
        if (boldSwitch != null) {
            boldSwitch.setChecked(isBold);
            boldSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                saveScrollPosition();
                prefs.edit().putBoolean("bold_text_enabled", isChecked).apply();
                recreate();
            });
        }

        setupBottomNavigation();
        setupButtons();
        setupScrollToTop();
    }

    private void setupBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_settings);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else if (id == R.id.nav_report) {
                startActivity(new Intent(this, CommunityReportActivity.class));
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("from_navigation", true);
                startActivity(intent);
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    private void setupButtons() {
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
        MaterialButton contactUsButton = findViewById(R.id.contactUsBtn);
        contactUsButton.setOnClickListener(view -> startActivity(new Intent(this, ContactUsActivity.class)));

        Button signoutBtn = findViewById(R.id.buttonSignOut);
        dialog = new Dialog(SettingsActivity.this);
        dialog.setContentView(R.layout.dialog_signout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogCancel = dialog.findViewById(R.id.signoutCancelBtn);
        dialogSignout = dialog.findViewById(R.id.signoutBtn);
        dialogCancel.setOnClickListener(v -> dialog.dismiss());
        dialogSignout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        signoutBtn.setOnClickListener(v -> dialog.show());
    }

    private void setupScrollToTop() {
        FloatingActionButton scrollToTopFab = findViewById(R.id.scrollToTopFab);
        scrollToTopFab.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (scrollView.getScrollY() > 300) scrollToTopFab.show();
            else scrollToTopFab.hide();
        });
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

    private void applyBoldToAllSwitches(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof Switch) ((TextView) child).setTypeface(null, Typeface.BOLD);
                applyBoldToAllSwitches(child);
            }
        }
    }

    private void applyBoldToAllWidgets(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof Button || child instanceof Switch) {
                    ((TextView) child).setTypeface(null, Typeface.BOLD);
                }
                applyBoldToAllWidgets(child);
            }
        }
    }

    private void applyFontScale() {
        Configuration configuration = new Configuration(getResources().getConfiguration());
        configuration.fontScale = textScale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        recreate();
    }

    private void updateScaleLabel() {
        textScaleLabel.setText((int) (textScale * 100) + "%");
    }

    private void saveScrollPosition() {
        if (scrollView != null) {
            int scrollY = scrollView.getScrollY();
            prefs.edit().putInt("scroll_pos", scrollY).apply();
        }
    }

    private void restoreScrollPosition() {
        savedPosition = prefs.getInt("scroll_pos", 0);
        if (isTaskRoot()) {
            savedPosition = 0;
        }
        scrollView.post(() -> scrollView.scrollTo(0, savedPosition));
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
}