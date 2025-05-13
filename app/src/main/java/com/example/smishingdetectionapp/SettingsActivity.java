// FINAL MERGED VERSION (READY TO PASTE)

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
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.smishingdetectionapp.chat.ChatAssistantActivity;
import com.example.smishingdetectionapp.news.NewsAdapter;
import com.example.smishingdetectionapp.ui.account.AccountActivity;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

        ScrollView scrollView = findViewById(R.id.settingsScroll);
        if (isBold) {
            applyBoldToAllSwitches(scrollView);
            applyBoldToAllWidgets(scrollView);
        }

        if (scrollView != null) {
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_UP));
        }

        Switch boldSwitch = findViewById(R.id.bold_text);
        if (boldSwitch != null) {
            boldSwitch.setChecked(isBold);
            boldSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int scrollY = scrollView.getScrollY();
                prefs.edit().putBoolean("bold_text_enabled", isChecked).putInt("scroll_pos", scrollY).apply();
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
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
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
        findViewById(R.id.communityBtn).setOnClickListener(v -> startActivity(new Intent(this, CommunityHomeActivity.class)));

        findViewById(R.id.share_button).setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this app! Download Smishing Detection App: https://example.com");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, null));
        });

        Button signoutBtn = findViewById(R.id.buttonSignOut);
        Intent intent = new Intent(this, LoginActivity.class);
        dialog = new Dialog(SettingsActivity.this);
        dialog.setContentView(R.layout.dialog_signout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogCancel = dialog.findViewById(R.id.signoutCancelBtn);
        dialogSignout = dialog.findViewById(R.id.signoutBtn);
        dialogCancel.setOnClickListener(v -> dialog.dismiss());
        dialogSignout.setOnClickListener(v -> {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        signoutBtn.setOnClickListener(v -> dialog.show());

        FloatingActionButton scrollToTopFab = findViewById(R.id.scrollToTopFab);
        scrollToTopFab.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (scrollView.getScrollY() > 300) scrollToTopFab.show();
            else scrollToTopFab.hide();
        });
    }

    private void triggerBiometricAuthenticationWithTimeout() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication Required")
                .setDescription("Please authenticate to access your account settings")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();
        biometricPrompt = getPrompt();
        biometricPrompt.authenticate(promptInfo);
        new Handler().postDelayed(() -> {
            if (!isAuthenticated) {
                notifyUser("Authentication timed out. Redirecting to Settings...");
                biometricPrompt.cancelAuthentication();
                redirectToSettingsActivity();
            }
        }, TIMEOUT_MILLIS);
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

    public void openNotificationsActivity(View view) {
        startActivity(new Intent(this, NotificationActivity.class));
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

    @Override
    public void onBackPressed() {
        findViewById(R.id.bottom_navigation).performClick();
        super.onBackPressed();
    }
}
