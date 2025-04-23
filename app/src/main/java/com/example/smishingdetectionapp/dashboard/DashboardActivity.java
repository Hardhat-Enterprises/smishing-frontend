package com.example.smishingdetectionapp.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.SharedActivity;
import com.example.smishingdetectionapp.dashboard.DashboardActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends SharedActivity {
    private static final String TAG = "DashboardActivity";

    private TextView tvTotalAttacks, tvSuccess, tvFailed, tvUniqueSenders, tvAvgContentLength;
    private BarChart daywiseChart;
    private PieChart categoryChart;
    private AnalyticsManager analyticsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Log.d(TAG, "onCreate called");

        // Setup Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_dashboard);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            } else if (id == R.id.nav_dashboard) {
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(DashboardActivity.this, NewsActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
                overridePendingTransition(0,0);
                finish();
                return true;
            }
            return false;
        });

        // Initialize AnalyticsManager
         analyticsManager = new AnalyticsManager(this);
        Log.d(TAG, "AnalyticsManager initialized");

        // Bind UI elements
        tvTotalAttacks     = findViewById(R.id.tvTotalAttacks);
        tvSuccess          = findViewById(R.id.tvSuccess);
        tvFailed           = findViewById(R.id.tvFailed);
        tvUniqueSenders    = findViewById(R.id.tvUniqueSenders);
        tvAvgContentLength = findViewById(R.id.tvAvgContentLength);
        daywiseChart       = findViewById(R.id.daywiseChart);
        categoryChart      = findViewById(R.id.categoryChart);

        // Load and display analytics data
         loadAnalyticsData();
    }

//    private void loadAnalyticsData() {
//        Log.d(TAG, "loadAnalyticsData called");
//        try {
//            // Fetch data
//            int totalAttacks      = analyticsManager.getTotalAttacks();
//            int successfulAttacks = analyticsManager.getSuccessfulAttacks();
//            int failedAttacks     = analyticsManager.getFailedAttacks();
//            int uniqueSenders     = analyticsManager.getUniqueSenderCount();
//            double avgLength      = analyticsManager.getAverageContentLength();
//            Log.d(TAG, "Fetched stats: total=" + totalAttacks + ", success=" + successfulAttacks + ", failed=" + failedAttacks);
//
//            // Display stats
//            tvTotalAttacks.setText("Total Attacks: " + totalAttacks);
//            tvSuccess.setText("Successful: " + successfulAttacks);
//            tvFailed.setText("Failed: " + failedAttacks);
//            tvUniqueSenders.setText("Unique Senders: " + uniqueSenders);
//            tvAvgContentLength.setText(String.format("Avg. SMS Length: %.1f", avgLength));
//
//            // Daywise Bar Chart
//            Map<String, Integer> daywiseMap = analyticsManager.getDaywiseAttacks();
//            List<BarEntry> barEntries = new ArrayList<>();
//            int index = 0;
//            for (Map.Entry<String, Integer> entry : daywiseMap.entrySet()) {
//                barEntries.add(new BarEntry(index++, entry.getValue()));
//            }
//            Log.d(TAG, "Daywise data points=" + barEntries.size());
//            BarDataSet barSet = new BarDataSet(barEntries, "Attacks per Day");
//            BarData barData = new BarData(barSet);
//            daywiseChart.setData(barData);
//            daywiseChart.getDescription().setEnabled(false);
//            daywiseChart.invalidate();
//
//            // Category Pie Chart
//            Map<String, Integer> catMap = analyticsManager.getAttackCategories();
//            List<PieEntry> pieEntries = new ArrayList<>();
//            for (Map.Entry<String, Integer> entry : catMap.entrySet()) {
//                pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
//            }
//            Log.d(TAG, "Category data points=" + pieEntries.size());
//            PieDataSet pieSet = new PieDataSet(pieEntries, "Attack Categories");
//            PieData pieData = new PieData(pieSet);
//            categoryChart.setData(pieData);
//            categoryChart.getDescription().setEnabled(false);
//            categoryChart.invalidate();
//        } catch (Exception e) {
//            Log.e(TAG, "Error loading analytics data", e);
//        }
//    }

    private void loadAnalyticsData() {
        Log.d(TAG, "loadAnalyticsData called");
        // Using static demo values until manager issue is fixed
        int totalAttacks      = 120;
        int successfulAttacks = 90;
        int failedAttacks     = 30;
        int uniqueSenders     = 45;
        double avgLength      = 150.0;
        Log.d(TAG, "Static stats: total=" + totalAttacks + ", success=" + successfulAttacks + ", failed=" + failedAttacks);

        // Display stats
        tvTotalAttacks.setText("Total Attacks: " + totalAttacks);
        tvSuccess.setText("Successful: " + successfulAttacks);
        tvFailed.setText("Failed: " + failedAttacks);
        tvUniqueSenders.setText("Unique Senders: " + uniqueSenders);
        tvAvgContentLength.setText(String.format("Avg. SMS Length: %.1f", avgLength));

        // Static Daywise Bar Chart data
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 10));
        barEntries.add(new BarEntry(1, 15));
        barEntries.add(new BarEntry(2, 20));
        barEntries.add(new BarEntry(3, 5));
        BarDataSet barSet = new BarDataSet(barEntries, "Attacks per Day");
        BarData barData = new BarData(barSet);
        daywiseChart.setData(barData);
        daywiseChart.getDescription().setEnabled(false);
        daywiseChart.invalidate();

        // Static Category Pie Chart data
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(60, "Phishing"));
        pieEntries.add(new PieEntry(40, "Smishing"));
        PieDataSet pieSet = new PieDataSet(pieEntries, "Attack Categories");
        PieData pieData = new PieData(pieSet);
        categoryChart.setData(pieData);
        categoryChart.getDescription().setEnabled(false);
        categoryChart.invalidate();
    }
}
