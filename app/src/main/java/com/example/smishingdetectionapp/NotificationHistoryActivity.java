package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            fetchNotificationHistory(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchNotificationHistory(String userId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<NotificationItem>> call = apiService.getNotificationHistory(userId);

        call.enqueue(new Callback<List<NotificationItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<NotificationItem>> call, @NonNull Response<List<NotificationItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationItem> notifications = response.body();
                    adapter = new NotificationAdapter(notifications);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(NotificationHistoryActivity.this, "No notifications found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NotificationItem>> call, @NonNull Throwable t) {
                Log.e("NotificationHistory", "API Failure: " + t.getMessage());
                Toast.makeText(NotificationHistoryActivity.this, "Failed to fetch notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
