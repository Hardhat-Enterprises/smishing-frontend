package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuardianActivity extends AppCompatActivity {

    private EditText editTextGuardian;
    private Button buttonSaveGuardian;
    private Button buttonNotifyGuardian;
    private Button viewHistoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        editTextGuardian = findViewById(R.id.editTextGuardian);
        buttonSaveGuardian = findViewById(R.id.buttonSaveGuardian);
        buttonNotifyGuardian = findViewById(R.id.buttonNotifyGuardian);
        viewHistoryBtn = findViewById(R.id.viewNotificationHistoryBtn);

        buttonSaveGuardian.setOnClickListener(view -> {
            String guardianEmail = editTextGuardian.getText().toString().trim();

            if (!guardianEmail.isEmpty()) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    addGuardian(currentUser.getUid(), guardianEmail);
                } else {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a guardian email", Toast.LENGTH_SHORT).show();
            }
        });

        buttonNotifyGuardian.setOnClickListener(view -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                triggerGuardianNotification(currentUser.getUid());
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });

        viewHistoryBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationHistoryActivity.class));
        });
    }

    private void addGuardian(String userId, String guardianEmail) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();
        body.put("guardianEmail", guardianEmail);

        Call<Void> call = apiService.addGuardian(userId, body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GuardianActivity.this, "Guardian linked successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GuardianActivity.this, "Link failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("GuardianLink", "Error: " + t.getMessage());
                Toast.makeText(GuardianActivity.this, "Network error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void triggerGuardianNotification(String userId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<Void> call = apiService.triggerNotification(userId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GuardianActivity.this, "Notification sent to guardian!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GuardianActivity.this, "Failed to send notification.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("GuardianNotify", "Error: " + t.getMessage(), t);
                Toast.makeText(GuardianActivity.this, "Network error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
