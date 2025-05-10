package com.example.smishingdetectionapp.riskmeter;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.content.pm.PackageManager;
import android.app.KeyguardManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.R;

import java.util.ArrayList;
import java.util.List;

public class RiskScannerLogic {

    private static int calculatedScore = 0;

    // this is to list the specific risk factors that were factored for our detected issues feedback
    private static final List<String> triggeredRisks = new ArrayList<>();

    public static void scanHabits(
            RiskResultActivity activity,
            ProgressBar progressBar,
            TextView riskLevelText,
            View lightAgeGroup, View lightSmsApp, View lightSecurityApp,
            View lightSpamFilter, View lightDeviceLock, View lightUnknownSources,
            View lightSmsBehaviour,
            boolean disableSmsRisk,
            boolean disableAgeRisk,
            boolean disableSecurityRisk
    ) {
        triggeredRisks.clear();
        int totalScore = 0;

        // our aged-based adjustments (hardcoded for now but with sign-up API this will be dynamic)
        if (!disableAgeRisk) {
            int userAge = 23;
            int ageGroupRisk = calculateAgeGroupRisk(userAge);
            totalScore += ageGroupRisk;
            if (ageGroupRisk > 0) {
                triggeredRisks.add("You are in a higher risk age group.");
            }
            setLightColor(lightAgeGroup, ageGroupRisk > 0 ? "#EF5350" : "#66BB6A");
        } else {
            setLightColor(lightAgeGroup, "#B0B0B0"); // grey (not applied)
        }

        // checking for sms risk behaviour
        if (!disableSmsRisk) {
            boolean smsBehaviorRisk = checkSmsBehavior(activity);
            totalScore += smsBehaviorRisk ? 15 : 0;
            if (smsBehaviorRisk) {
                triggeredRisks.add("Some SMS messages on your device appear to be potentially suspicious.");
            }
            setLightColor(lightSmsBehaviour, smsBehaviorRisk ? "#EF5350" : "#66BB6A");
        } else {
            setLightColor(lightSmsBehaviour, "#B0B0B0");
        }

        // checking for security apps, spam filters, and lock screen
        if (!disableSecurityRisk) {
            boolean securityAppInstalled = checkSecurityApps(activity);
            totalScore += securityAppInstalled ? 0 : 14;
            if (!securityAppInstalled) {
                triggeredRisks.add("No trusted security apps were found on your device.");
            }
            setLightColor(lightSecurityApp, securityAppInstalled ? "#66BB6A" : "#EF5350");

            boolean spamFilterInstalled = checkSpamFilterApp(activity);
            totalScore += spamFilterInstalled ? 0 : 14;
            if (!spamFilterInstalled) {
                triggeredRisks.add("No spam filter was detected on your device.");
            }
            setLightColor(lightSpamFilter, spamFilterInstalled ? "#66BB6A" : "#EF5350");

            boolean deviceSecured = checkDeviceLock(activity);
            totalScore += deviceSecured ? 0 : 14;
            if (!deviceSecured) {
                triggeredRisks.add("Your device has no lock screen (PIN or password).");
            }
            setLightColor(lightDeviceLock, deviceSecured ? "#66BB6A" : "#EF5350");
        } else {
            setLightColor(lightSecurityApp, "#B0B0B0");
            setLightColor(lightSpamFilter, "#B0B0B0");
            setLightColor(lightDeviceLock, "#B0B0B0");
        }

        // unknown sources enabled
        boolean unknownSourcesEnabled = checkUnknownSources(activity);
        totalScore += unknownSourcesEnabled ? 14 : 0;
        if (unknownSourcesEnabled) {
            triggeredRisks.add("Your device allows app installations from unknown sources.");
        }
        setLightColor(lightUnknownSources, unknownSourcesEnabled ? "#EF5350" : "#66BB6A");

        // sms app trusted or not
        boolean trustedSmsApp = checkTrustedSmsApp(activity);
        totalScore += trustedSmsApp ? 0 : 14;
        if (!trustedSmsApp) {
            triggeredRisks.add("Your current SMS app may not be from a trusted provider.");
        }
        setLightColor(lightSmsApp, trustedSmsApp ? "#66BB6A" : "#EF5350");

        // limiting score to max 100
        if (totalScore > 100) totalScore = 100;

        calculatedScore = totalScore;

        updateRiskLevel(riskLevelText, totalScore);
        updateProgressBarColor(progressBar, totalScore);
        activity.animateProgress(progressBar, activity.percentageText, totalScore);
    }

    public static int getCalculatedScore() {
        return calculatedScore;
    }

    public static List<String> getTriggeredRisks() {
        return triggeredRisks;
    }

    // colour coordinating the progress bar based on risk level
    private static void updateProgressBarColor(ProgressBar progressBar, int score) {
        if (score <= 30) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(progressBar.getContext(), R.color.green));
        } else if (score <= 60) {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(progressBar.getContext(), R.color.orange));
        } else {
            progressBar.setProgressTintList(ContextCompat.getColorStateList(progressBar.getContext(), R.color.redd));
        }
    }

    // dynamically changing risk level text based on score
    private static void updateRiskLevel(TextView riskLevelText, int totalScore) {
        String riskLevel;
        if (totalScore <= 30) {
            riskLevel = "Low Risk";
        } else if (totalScore <= 60) {
            riskLevel = "Moderate Risk";
        } else {
            riskLevel = "High Risk";
        }
        riskLevelText.setText(riskLevel);
    }

    // our scores for the ages
    private static int calculateAgeGroupRisk(int age) {
        if (age >= 18 && age <= 24) return 15;
        if (age >= 25 && age <= 34) return 10;
        return 0;
    }

    private static boolean checkSmsBehavior(Context context) {
        return false;  // this is for aaliyan to contribute to
    }

    // checking for popular security apps downloaded
    private static boolean checkSecurityApps(Context context) {
        PackageManager pm = context.getPackageManager();
        String[] knownSecurityApps = {"com.norton.mobilesecurity", "com.mcafee.android", "com.bitdefender.antivirus"};
        for (String app : knownSecurityApps) {
            try {
                pm.getPackageInfo(app, 0);
                return true;
            } catch (PackageManager.NameNotFoundException ignored) { }
        }
        return false;
    }

    // checking if spam filter could be enabled as with google messages or other apps
    private static boolean checkSpamFilterApp(Context context) {
        PackageManager pm = context.getPackageManager();
        String[] spamFilters = {"com.google.android.apps.messaging", "com.mrnumber.blocker", "com.truecaller"};
        for (String app : spamFilters) {
            try {
                pm.getPackageInfo(app, 0);
                return true;
            } catch (PackageManager.NameNotFoundException ignored) { }
        }
        return false;
    }

    private static boolean checkDeviceLock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km != null && km.isDeviceSecure();
    }

    // this will depend on the android version currently we can check settings
    private static boolean checkUnknownSources(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        } else {
            try {
                return Settings.Secure.getInt(context.getContentResolver(),
                        Settings.Secure.INSTALL_NON_MARKET_APPS) == 1;
            } catch (Settings.SettingNotFoundException e) {
                return false;
            }
        }
    }

    private static boolean checkTrustedSmsApp(Context context) {
        String defaultSmsApp = Settings.Secure.getString(context.getContentResolver(), "sms_default_application");
        return defaultSmsApp != null && (
                defaultSmsApp.contains("messages") || defaultSmsApp.contains("samsung")
        );
    }

    private static void setLightColor(View view, String color) {
        view.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(color)));
    }

    private static String getColorForRisk(int risk) {
        if (risk <= 30) return "#66BB6A";  // green
        if (risk <= 60) return "#FFEB3B";  // yellow
        return "#EF5350";                 // red
    }
}
