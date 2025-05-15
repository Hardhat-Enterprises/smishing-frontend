package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.databinding.ActivityMainBinding;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.detections.DetectionsActivity;
import com.example.smishingdetectionapp.notifications.NotificationPermissionDialogFragment;
import com.example.smishingdetectionapp.riskmeter.RiskScannerTCActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends SharedActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private boolean isBackPressed = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_report, R.id.nav_news, R.id.nav_settings)
                .build();

        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }

        // 🔄 Force delete old token and fetch a new one
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FCM", "Token deleted. Now requesting new one...");
                FirebaseMessaging.getInstance().getToken()
                        .addOnSuccessListener(token -> {
                            Log.d("FCM", "New FCM Token (forced): " + token);
                            sendFcmTokenToServer("your-jwt-token-here", token); // replace token after login
                        })
                        .addOnFailureListener(e -> Log.e("FCM", "Failed to get new token", e));
            } else {
                Log.e("FCM", "Token deletion failed", task.getException());
            }
        });

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_report) {
                startActivity(new Intent(getApplicationContext(), CommunityReportActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        findViewById(R.id.debug_btn).setOnClickListener(v -> startActivity(new Intent(this, DebugActivity.class)));
        findViewById(R.id.detections_btn).setOnClickListener(v -> {
            startActivity(new Intent(this, DetectionsActivity.class));
            finish();
        });
        findViewById(R.id.learn_more_btn).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EducationActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.scanner_btn).setOnClickListener(v -> {
            startActivity(new Intent(this, RiskScannerTCActivity.class));
            finish();
        });

        DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
        db.open();
        TextView totalCount = findViewById(R.id.total_counter);
        totalCount.setText("" + db.getCounter());
        db.close();
    }

    @Override
    public void onBackPressed() {
        if (isBackPressed) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            isBackPressed = true;
            new Handler().postDelayed(() -> isBackPressed = false, 2000);
        }
    }

    private boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void showNotificationPermissionDialog() {
        NotificationPermissionDialogFragment dialog = new NotificationPermissionDialogFragment();
        dialog.show(getSupportFragmentManager(), "notificationPermission");
    }

    private void sendFcmTokenToServer(String jwtToken, String fcmToken) {
        ApiService apiService = RetrofitClient
                .getClient("http://10.0.2.2:3000/api/")
                .create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();
        body.put("fcmToken", fcmToken);

        Call<Void> call = apiService.saveFcmToken("Bearer " + jwtToken, body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("FCM", "FCM token sent to server!");
                } else {
                    Log.e("FCM", "Server rejected FCM token");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FCM", "Error sending FCM token: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
