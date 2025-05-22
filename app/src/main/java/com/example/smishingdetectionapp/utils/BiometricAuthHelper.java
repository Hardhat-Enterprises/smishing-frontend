package com.example.smishingdetectionapp.utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.detections.DetectionsActivity;
import java.util.concurrent.Executor;
public class BiometricAuthHelper {
    public static void triggerBiometricAuthenticationWithTimeout(Activity activity) {
        if (!(activity instanceof FragmentActivity)) {
            Toast.makeText(activity, "Biometric auth not supported on this screen.", Toast.LENGTH_SHORT).show();
            return;
        }
        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        Executor executor = ContextCompat.getMainExecutor(activity);
        final boolean[] isAuthenticated = {false}; // Use array to allow modification inside inner class
        BiometricPrompt biometricPrompt = new BiometricPrompt(fragmentActivity, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                if (!isAuthenticated[0]) {
                    Toast.makeText(activity, "Authentication Error: " + errString, Toast.LENGTH_SHORT).show();
                    redirectTo(activity, MainActivity.class);
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                isAuthenticated[0] = true;
                Toast.makeText(activity, "Authentication Succeeded!", Toast.LENGTH_SHORT).show();
                redirectTo(activity, DetectionsActivity.class);
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(activity, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        });
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication Required")
                .setDescription("Please authenticate to proceed")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    private static void redirectTo(Activity activity, Class<?> target) {
        Intent intent = new Intent(activity, target);
        activity.startActivity(intent);
        activity.finish();
    }
}
