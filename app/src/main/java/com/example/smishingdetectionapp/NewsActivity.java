package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.dashboard.DashboardActivity;
import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.news.Models.RSSFeedModel;
import com.example.smishingdetectionapp.news.NewsAdapter;
import com.example.smishingdetectionapp.news.NewsRequestManager;
import com.example.smishingdetectionapp.news.OnFetchDataListener;
import com.example.smishingdetectionapp.news.SavedNewsActivity;
import com.example.smishingdetectionapp.news.SelectListener;
import com.example.smishingdetectionapp.notifications.NotificationType;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class NewsActivity extends SharedActivity implements SelectListener {
    RecyclerView recyclerView;
    NewsAdapter adapter; // moved to class scope to reuse
    NewsRequestManager manager;
    ProgressBar progressBar;
    TextView errorMessage;
    Button refreshButton, savedNewsButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
      
        // UI refs
        errorMessage = findViewById(R.id.errorTextView);
        recyclerView = findViewById(R.id.news_recycler_view);
        refreshButton = findViewById(R.id.refreshButton);
        savedNewsButton = findViewById(R.id.btn_saved_news); // new
        progressBar = findViewById(R.id.progressBar);

        // Saved News button click → open SavedNewsActivity
        savedNewsButton.setOnClickListener(v -> {
            Intent intent = new Intent(NewsActivity.this, SavedNewsActivity.class);
            startActivity(intent);
        });

        // Bottom navigation setup
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_news);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
<<<<<<< HEAD
            } else if (id == R.id.nav_dashboard) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
=======

            } else if (menuItem.getItemId() == R.id.nav_report) {
                startActivity(new Intent(this, CommunityReportActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
                
>>>>>>> upstream/dev
            } else if (id == R.id.nav_news) {
                nav.setActivated(true);
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });


 

        // Fetch articles
        // Initialize ProgressBar and set it visible before fetching data
        
        progressBar.setVisibility(View.VISIBLE);

        // Initialize RecyclerView and Adapter ONCE
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new NewsAdapter(this, this);
        recyclerView.setAdapter(adapter);
        
        fetchArticles();    // first load
        
        /*
        manager = new NewsRequestManager(this);
        manager.fetchRSSFeed(new OnFetchDataListener<RSSFeedModel.Feed>() {
            @Override
            public void onFetchData(List<RSSFeedModel.Article> list, String message) {
                adapter.submitList(list); // Only update data, don't reassign adapter
                progressBar.setVisibility(View.GONE); // Hide ProgressBar after fetching data

            }

            @Override
            public void onError(String message) {
                errorMessage.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            private void showNews(List<RSSFeedModel.Article> list) {
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(NewsActivity.this, 1));
                adapter = new NewsAdapter(NewsActivity.this, list, NewsActivity.this);
                recyclerView.setAdapter(adapter);
            }

        });*/

        // Refresh button click
        refreshButton.setOnClickListener(v -> {
            if (isNetworkConnected()) {
                fetchArticles(); 
            } else {
                Toast.makeText(this, "You Have Lost Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

     /** Connectivity helper */
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            );
        }
        return false;
    }
  

    /** Fetch RSS feed */
    private void fetchArticles() {
        progressBar.setVisibility(View.VISIBLE);
        manager = new NewsRequestManager(this);
        manager.fetchRSSFeed(new OnFetchDataListener<RSSFeedModel.Feed>() {
            @Override
            public void onFetchData(List<RSSFeedModel.Article> list, String msg) {
                adapter.submitList(list);
                progressBar.setVisibility(View.GONE);

                //for notification function
                if (list != null && !list.isEmpty()) {
                    checkAndNotifyLatestNews(list.get(0)); // Check the newest news
                }
            }
            @Override
            public void onError(String message) {
                errorMessage.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void OnNewsClicked(RSSFeedModel.Article article) {
        if (article != null && article.link != null && !article.link.isEmpty()) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.link));
                startActivity(browserIntent);
            } catch (Exception e) {
                Log.e("NewsActivity", "Error opening URL", e);
                Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No URL available", Toast.LENGTH_SHORT).show();
        }
    }

       /** Hardware back – bounce to Home tab */
    @Override
    public void onBackPressed() {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        super.onBackPressed();
    }

    //Notification
    private void checkAndNotifyLatestNews(RSSFeedModel.Article latestArticle) {
        SharedPreferences prefs = getSharedPreferences("NewsPrefs", MODE_PRIVATE);
        String lastTitle = prefs.getString("last_notified_title", "");

        // Check notification enabled or not (in notification settings)
        boolean isNewsNotificationEnabled = NotificationType.createNewsAlert(getApplicationContext()).getEnabled();

        if (isNewsNotificationEnabled && !latestArticle.title.equals(lastTitle)) {
            // Send notification
            showNotification("Cyber News Update", latestArticle.title);

            // Save the newest title
            prefs.edit().putString("last_notified_title", latestArticle.title).apply();
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "news_channel_id";
        String channelName = "News Notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.new_logo)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }


}
