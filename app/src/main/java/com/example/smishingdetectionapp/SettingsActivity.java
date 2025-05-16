package com.example.smishingdetectionapp;

import android.app.Dialog;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.Community.CommunityHomeActivity;
import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.chat.ChatAssistantActivity;
import com.example.smishingdetectionapp.dashboard.DashboardActivity;
import com.example.smishingdetectionapp.news.NewsAdapter;
import com.example.smishingdetectionapp.ui.account.AccountActivity;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.concurrent.Executor;
import android.widget.ScrollView;
import android.graphics.Typeface;
import android.view.ViewGroup;

import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.widget.Switch;
import com.example.smishingdetectionapp.ui.ContactUsActivity;
import com.google.android.material.button.MaterialButton;



public class SettingsActivity extends AppCompatActivity {
    private SeekBar seekBarFontScale;
    private TextView preview;
    private static final int TIMEOUT_MILLIS = 10000; // 30 seconds timeout
    private boolean isAuthenticated = false;
    private BiometricPrompt biometricPrompt; // To cancel authentication
    private Button buttonIncreaseTextSize, buttonDecreaseTextSize, dialogCancel, dialogSignout;
    private TextView textScaleLabel;
    private float textScale; // between 0.8f and 1.5f, for example
    private Dialog dialog;
    private static final String KEY_SCROLL_POSITION = "scroll_position";
    private int savedPosition = 0;
    private ScrollView scrollView;
    private SharedPreferences prefs;
    private boolean isColdStart = true;
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
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            prefs.edit().putBoolean("dark_mode", isChecked).apply();

            // Optional: recreate activity to apply theme changes immediately
            recreate();
        });


        textScaleLabel = findViewById(R.id.textScaleLabel);
        seekBarFontScale = findViewById(R.id.seekBarFontScale);
        textScale = PreferencesUtil.getTextScale(this);
        updateScaleLabel();



// Set current SeekBar position
        seekBarFontScale.setProgress((int) (textScale * 10));

        seekBarFontScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float newScale = progress / 10f;

                // Clamp between 0.8f and 1.5f
                if (newScale < 0.8f) newScale = 0.8f;
                if (newScale > 1.5f) newScale = 1.5f;

                textScale = newScale;
                PreferencesUtil.setTextScale(SettingsActivity.this, textScale);
                updateScaleLabel();
                applyFontScale();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if (isBold) {
            applyBoldToAllSwitches(findViewById(R.id.settingsScroll));
        }
        if (isBold) {
            applyBoldToAllWidgets(findViewById(R.id.settingsScroll));
        }

        scrollView = findViewById(R.id.settingsScroll);

        //Cold start/ navigation icon
        boolean isFromNav = getIntent().getBooleanExtra("from_navigation", false);
        boolean isCold = prefs.getBoolean("cold_start", true);

        if (isFromNav || isCold) {
            scrollView.post(() -> scrollView.scrollTo(0, 0));
            prefs.edit().putBoolean("cold_start", false).apply();  // 冷启动处理完毕
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

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);

        nav.setSelectedItemId(R.id.nav_settings);

        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (menuItem.getItemId() == R.id.nav_report) {
                Intent i = new Intent(this, CommunityReportActivity.class);
                i.putExtra("source", "home");
                startActivity(i);
                overridePendingTransition(0,0);
                finish();
                return true;

            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("from_navigation", true);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        // Account button to switch to account page with biometric authentication
        Button accountBtn = findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(v -> triggerBiometricAuthenticationWithTimeout());

        //Notification button to switch to notification page
        Button notificationBtn = findViewById(R.id.notificationBtn);
        notificationBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        //Filtering button to switch to Smishing rules page
        ImageView filteringBtn = findViewById(R.id.imageView7);
        if (filteringBtn != null) {
            filteringBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, SmishingRulesActivity.class));
            });
        }

        // Report button to switch to reporting page
        Button reportBtn = findViewById(R.id.reportBtn);
        reportBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, CommunityReportActivity.class));
        });


        // Help button to switch to Help page
        Button helpBtn = findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpActivity.class));
        });

        // About Me button to switch to AboutMeActivity
        Button aboutMeButton = findViewById(R.id.aboutMeBtn);
        aboutMeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AboutMeActivity.class);
            startActivity(intent);
        });


        Button aboutUsBtn = findViewById(R.id.aboutUsBtn);
        aboutUsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });

        MaterialButton contactUsButton = findViewById(R.id.contactUsBtn);
        contactUsButton.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ContactUsActivity.class);
            startActivity(intent);
        });



        Button chatAssistantBtn = findViewById(R.id.chatAssistantBtn);
        chatAssistantBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChatAssistantActivity.class);
            startActivity(intent);
        });

        //Feedback Button to switch to Feedback page
        Button feedbackBtn = findViewById(R.id.feedbackBtn);
        feedbackBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackActivity.class));
        });

        //Community Button to switch to Community page
        Button communityBtn = findViewById(R.id.communityBtn);
        communityBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, CommunityHomeActivity.class);
            i.putExtra("source", "settings");
            startActivity(i);
        });

        Button signoutBtn = findViewById(R.id.buttonSignOut);
        Intent intent = new Intent(this, LoginActivity.class);
        dialog = new Dialog(SettingsActivity.this);
        dialog.setContentView(R.layout.dialog_signout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogCancel = dialog.findViewById(R.id.signoutCancelBtn);
        dialogSignout = dialog.findViewById(R.id.signoutBtn);

        dialogCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialogSignout.setOnClickListener(v -> {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        signoutBtn.setOnClickListener(v -> {
            dialog.show();
        });
        if (isTaskRoot()) {
            prefs.edit().putBoolean("cold_start", true).apply();
            prefs.edit().remove("scroll_pos").apply();
        }


    }

    // Trigger biometric authentication with timeout
    private void triggerBiometricAuthenticationWithTimeout() {
        int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
                | BiometricManager.Authenticators.DEVICE_CREDENTIAL;

        BiometricManager bm = BiometricManager.from(this);
        switch (bm.canAuthenticate(authenticators)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // safe to ask for biometrics / device PIN
                biometricPrompt = getPrompt();
                biometricPrompt.authenticate(buildPromptInfo(authenticators));
                startTimeoutTimer();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // nothing enrolled → just open the Account screen (or send user to settings)
                openAccountActivity();
                break;

            default:
                // sensor missing, locked out, etc. → fall back or notify
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

    // BiometricPrompt setup
    private BiometricPrompt getPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Authentication Error: " + errString);
                redirectToSettingsActivity();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication Succeeded!");
                isAuthenticated = true; // Mark as authenticated
                openAccountActivity(); // Proceed to AccountActivity
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Authentication Failed");
            }
        };

        return new BiometricPrompt(this, executor, callback);
    }

    // Start a timeout timer for authentication
    private void startTimeoutTimer() {
        new Handler().postDelayed(() -> {
            if (!isAuthenticated) { // If authentication hasn't occurred within the timeout
                notifyUser("Authentication timed out. Redirecting to Settings...");
                biometricPrompt.cancelAuthentication(); // Cancel the ongoing authentication
                redirectToSettingsActivity(); // Redirect to SettingsActivity on timeout
            }
        }, TIMEOUT_MILLIS);
    }

    // Redirect to SettingsActivity
    private void redirectToSettingsActivity() {
        Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish(); // Ensure the current activity is closed
    }

    // Open AccountActivity
    private void openAccountActivity() {
        Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
        startActivity(intent);
        finish(); // Close SettingsActivity if AccountActivity is opened
    }

    // Show a toast message
    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Notification button to switch to notification page
    public void openNotificationsActivity(View view) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    private void applyBoldToAllSwitches(View root) {
        if (!(root instanceof ViewGroup)) return;

        ViewGroup group = (ViewGroup) root;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);

            if (child instanceof android.widget.Switch || child instanceof androidx.appcompat.widget.SwitchCompat) {
                ((TextView) child).setTypeface(null, Typeface.BOLD);
            }

            applyBoldToAllSwitches(child);
        }
    }

    private void applyBoldToAllWidgets(View root) {
        if (!(root instanceof ViewGroup)) return;

        ViewGroup group = (ViewGroup) root;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);

            // ✅ Bold Switch labels
            if (child instanceof android.widget.Switch || child instanceof androidx.appcompat.widget.SwitchCompat) {
                ((TextView) child).setTypeface(null, Typeface.BOLD);
            }

            // ✅ Bold Buttons (MaterialButton, Button, etc.)
            if (child instanceof android.widget.Button ||
                    child instanceof com.google.android.material.button.MaterialButton) {
                ((TextView) child).setTypeface(null, Typeface.BOLD);
            }

            // Recursively apply to nested children
            applyBoldToAllWidgets(child);
        }
    }

    private void applyFontScale() {
        Configuration configuration = getResources().getConfiguration();
        configuration = new Configuration(configuration); // make a copy
        configuration.fontScale = textScale;

        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        // Refresh the layout
        recreate();
    }

    private void saveAndApplyScale() {
        PreferencesUtil.setTextScale(this, textScale);
        updateScaleLabel();
        applyFontScale();
    }

    private void updateScaleLabel() {
        int percentage = (int) (textScale * 100);
        textScaleLabel.setText(percentage + "%");
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        finish();
        super.onBackPressed();
    }

    private void saveScrollPosition() {
        if (scrollView != null) {
            int scrollY = scrollView.getScrollY();
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putInt("scroll_pos", scrollY)
                    .apply();
        }
    }

    private void restoreScrollPosition() {
        savedPosition = prefs.getInt("scroll_pos", 0);

        if (isTaskRoot()) {
            savedPosition = 0;
        }

        scrollView.post(() ->
                scrollView.scrollTo(0, savedPosition)
        );
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

