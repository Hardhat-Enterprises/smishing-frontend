package com.example.smishingdetectionapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.TypedValue;
import com.example.smishingdetectionapp.PreferencesUtil;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.chat.ChatAssistantActivity;
import com.example.smishingdetectionapp.news.NewsAdapter;
import com.example.smishingdetectionapp.ui.account.AccountActivity;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.Executor;
import android.widget.ScrollView;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.widget.Switch;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isBold = prefs.getBoolean("bold_text_enabled", false);
        setTheme(isBold ? R.style.Theme_SmishingDetectionApp_Bold : R.style.Theme_SmishingDetectionApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        if (isBold) {
            applyBoldToAllSwitches(findViewById(R.id.settingsScroll));
        }
        if (isBold) {
            applyBoldToAllWidgets(findViewById(R.id.settingsScroll));
        }


        ScrollView scrollView = findViewById(R.id.settingsScroll);
        if (scrollView != null) {
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_UP));
        }


        Switch boldSwitch = findViewById(R.id.bold_text);
        if (boldSwitch != null) {
            boldSwitch.setChecked(isBold);
            boldSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int scrollY = scrollView != null ? scrollView.getScrollY() : 0;
                prefs.edit()
                        .putBoolean("bold_text_enabled", isChecked)
                        .putInt("scroll_pos", scrollY)
                        .apply();
                recreate(); // Reload to apply new theme
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
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                nav.setActivated(true);
                return true;
            }
            return false;
        });
        // Account button to switch to account page with biometric authentication
        Button accountBtn = findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(v -> triggerBiometricAuthenticationWithTimeout());

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
            startActivity(new Intent(this, ReportingActivity.class));
        });
        //Notification button to switch to notification page

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
            startActivity(new Intent(this, CommunityHomeActivity.class));
        });
        // share app button
        Button shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this app! Download Smishing Detection App: https://example.com");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        signoutBtn.setOnClickListener(v -> {
            dialog.show();
        });
        FloatingActionButton scrollToTopFab = findViewById(R.id.scrollToTopFab);
        scrollToTopFab.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (scrollView.getScrollY() > 300) {
                scrollToTopFab.show();
            } else {
                scrollToTopFab.hide();
            }
        });



    }


    // Trigger biometric authentication with timeout
    private void triggerBiometricAuthenticationWithTimeout() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication Required")
                .setDescription("Please authenticate to access your account settings")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        // Start the authentication process
        biometricPrompt = getPrompt();
        biometricPrompt.authenticate(promptInfo);

        // Start the timeout timer
        startTimeoutTimer();
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
}

