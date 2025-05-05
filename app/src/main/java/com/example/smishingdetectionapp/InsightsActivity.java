package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class InsightsActivity extends AppCompatActivity {

    private TextView totalDetectionsTextView;
    private TextView mostTargetedYearTextView;
    private TextView riskLevelTextView;
    private TextView threatPatternTextView;
    private TextView dateWiseLogTextView;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);

        ImageButton backButton = findViewById(R.id.insights_back);
        backButton.setOnClickListener(v -> finish());

        totalDetectionsTextView = findViewById(R.id.totalDetectionsTextView);
        mostTargetedYearTextView = findViewById(R.id.mostTargetedYearTextView);
        riskLevelTextView = findViewById(R.id.riskLevelTextView);
        threatPatternTextView = findViewById(R.id.threatPatternTextView);
        dateWiseLogTextView = findViewById(R.id.dateWiseLogTextView);
        barChart = findViewById(R.id.barChart);

        loadInsightsData(); // Load data initially
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInsightsData(); // Refresh when returning to the screen
    }

    private void loadInsightsData() {
        DatabaseAccess dbAccess = DatabaseAccess.getInstance(getApplicationContext());
        dbAccess.open();

        int total = dbAccess.getTotalDetections();
        String trends = dbAccess.getDetectionsGroupedByDate();
        dbAccess.close();

        totalDetectionsTextView.setText("Total Detections: " + total);
        dateWiseLogTextView.setText(trends);

        HashMap<String, Integer> yearlyCounts = new HashMap<>();
        HashMap<String, Integer> monthCounts = new HashMap<>();
        String latestDate = "";

        String[] lines = trends.split("\n");
        for (String line : lines) {
            if (line.matches("^\\d{4}-\\d{2}:.*")) {
                String year = line.substring(0, 4);
                yearlyCounts.put(year, yearlyCounts.getOrDefault(year, 0) + 1);

                String month = line.substring(0, 7);
                monthCounts.put(month, monthCounts.getOrDefault(month, 0) + 1);

                if (latestDate.isEmpty()) {
                    latestDate = line.substring(0, 10);
                }
            }
        }

        String mostTargetedYear = "-";
        int mostDetections = 0;
        for (String y : yearlyCounts.keySet()) {
            int count = yearlyCounts.get(y);
            if (count > mostDetections) {
                mostDetections = count;
                mostTargetedYear = y;
            }
        }
        mostTargetedYearTextView.setText("Most Targeted Year: " + mostTargetedYear + " (" + mostDetections + " detections)");

        String mostActiveMonth = "-";
        int maxMonthCount = 0;
        for (String m : monthCounts.keySet()) {
            int count = monthCounts.get(m);
            if (count > maxMonthCount) {
                maxMonthCount = count;
                mostActiveMonth = m;
            }
        }
        threatPatternTextView.setText("Most Active Month: " + mostActiveMonth + " (" + maxMonthCount + " detections)");

        String riskLevel = getRiskLevel(latestDate);
        riskLevelTextView.setText("Risk Level: " + riskLevel);
        if ("High".equalsIgnoreCase(riskLevel)) {
            riskLevelTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else if ("Medium".equalsIgnoreCase(riskLevel)) {
            riskLevelTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
        } else {
            riskLevelTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }

        setupBarChart(yearlyCounts);
    }

    private void setupBarChart(HashMap<String, Integer> yearlyCounts) {
        List<String> sortedYears = new ArrayList<>(yearlyCounts.keySet());
        Collections.sort(sortedYears);

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (String year : sortedYears) {
            entries.add(new BarEntry(index, yearlyCounts.get(year)));
            labels.add(year);
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Detections per Year");
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(45f);

        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private String getRiskLevel(String latestDate) {
        if (latestDate.isEmpty()) return "Unknown";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date latest = sdf.parse(latestDate);
            long diff = new Date().getTime() - latest.getTime();
            long days = diff / (1000 * 60 * 60 * 24);

            if (days <= 90) return "High";
            else if (days <= 180) return "Medium";
            else return "Low";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}
