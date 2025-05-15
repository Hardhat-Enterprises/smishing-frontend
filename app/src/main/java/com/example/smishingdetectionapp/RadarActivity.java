package com.example.smishingdetectionapp;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

public class RadarActivity extends AppCompatActivity {

    private TextView radarStatus, tipBanner, lastUpdated;
    private Handler handler = new Handler();
    private Map<String, Integer> categoryCounts = new LinkedHashMap<>();
    private Map<String, List<String>> messageSamples = new HashMap<>();
    private int index = 0;
    private String[] categoryOrder;
    private BarChart barChart;
    private PieChart pieChart;
    private Spinner categoryFilter;
    private final Map<String, Integer> categoryColors = new HashMap<>();
    private String[] tips = {
            "🚫 Never click on unknown links.",
            "🔒 Enable spam filters in your messaging app.",
            "📵 Ignore suspicious SMS from unknown senders.",
            "🧠 Be cautious of messages asking for personal info."
    };
    private int tipIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        radarStatus = findViewById(R.id.radarStatus);
        tipBanner = findViewById(R.id.tipBanner);
        lastUpdated = findViewById(R.id.lastUpdated);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        categoryFilter = findViewById(R.id.region_filter);

        ImageButton backButton = findViewById(R.id.radar_back);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(RadarActivity.this, MainActivity.class));
            finish();
        });

        setupCategoryFilter();
        setupCategoryColors();
        rotateTipBanner();
        fetchDetectionsPeriodically();
    }

    private void setupCategoryFilter() {
        List<String> filters = Arrays.asList("All Categories", "Banking", "Delivery", "Insurance", "Phishing", "Other");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters);
        categoryFilter.setAdapter(adapter);
    }

    private void setupCategoryColors() {
        categoryColors.put("Phishing", 0xFF03A9F4); // Light Blue
        categoryColors.put("Banking", 0xFF9C27B0);  // Purple
        categoryColors.put("Delivery", 0xFF4CAF50); // Green
        categoryColors.put("Other", 0xFFFFC107);    // Yellow
        categoryColors.put("Insurance", 0xFFFF5722); // Orange
    }

    private void fetchDetectionsPeriodically() {
        handler.postDelayed(() -> {
            loadDetections();
            runCategoryCycle();
            generateCharts();
            fetchDetectionsPeriodically();
        }, 10000);
    }

    private void loadDetections() {
        categoryCounts.clear();
        messageSamples.clear();

        DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
        db.open();
        Cursor cursor = db.getAllDetections();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String message = cursor.getString(cursor.getColumnIndexOrThrow("Message"));
                String category = categorizeMessageContent(message);
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
                messageSamples.computeIfAbsent(category, k -> new ArrayList<>()).add(message);
            }
            cursor.close();
        }

        db.close();
        categoryOrder = categoryCounts.keySet().toArray(new String[0]);
    }

    private String categorizeMessageContent(String message) {
        if (message == null) return "Other";
        message = message.toLowerCase();

        if (message.contains("account") || message.contains("bank") || message.contains("login"))
            return "Banking";
        if (message.contains("parcel") || message.contains("delivery") || message.contains("courier"))
            return "Delivery";
        if (message.contains("insurance") || message.contains("medicare") || message.contains("policy"))
            return "Insurance";
        if (message.contains("click") || message.contains("win") || message.contains("prize") || message.contains("verify"))
            return "Phishing";

        return "Other";
    }

    private void runCategoryCycle() {
        if (categoryCounts.isEmpty()) {
            radarStatus.setText("No smishing activity detected.");
            return;
        }

        String selectedCategory = categoryFilter.getSelectedItem().toString();
        String category = "All Categories".equals(selectedCategory) ? categoryOrder[index] : selectedCategory;

        int count = categoryCounts.getOrDefault(category, 0);
        String alertLevel = count >= 4 ? "🔴 High" : count >= 2 ? "⚠️ Alert" : "🟢 Low";
        radarStatus.setText(alertLevel + " activity in: " + category + " (" + count + " detections)");

        if (count >= 2) animatePulse(radarStatus);

        String timestamp = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        lastUpdated.setText("Last updated: " + timestamp);

        index = (index + 1) % categoryOrder.length;
    }

    private void generateCharts() {
        List<BarEntry> barEntries = new ArrayList<>();
        List<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> barColors = new ArrayList<>();
        List<Integer> pieColors = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int i = 0;
        String selected = categoryFilter.getSelectedItem().toString();

        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            if (!selected.equals("All Categories") && !entry.getKey().equals(selected)) continue;
            barEntries.add(new BarEntry(i, entry.getValue()));
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
            labels.add(entry.getKey());
            int color = categoryColors.getOrDefault(entry.getKey(), ColorTemplate.MATERIAL_COLORS[i % ColorTemplate.MATERIAL_COLORS.length]);
            barColors.add(color);
            pieColors.add(color);
            i++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Scam Detections by Category");
        barDataSet.setColors(barColors);
        barChart.setData(new BarData(barDataSet));
        barChart.setDescription(new Description());
        barChart.invalidate();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos = (int) e.getX();
                if (pos < labels.size()) showMessageDialog(labels.get(pos));
            }
            @Override public void onNothingSelected() {}
        });

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(pieColors);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(48f);
        pieChart.setEntryLabelTextSize(10f);
        pieChart.setEntryLabelColor(getResources().getColor(android.R.color.white));
        pieChart.setDescription(new Description());
        pieChart.invalidate();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pe = (PieEntry) e;
                showMessageDialog(pe.getLabel());
            }
            @Override public void onNothingSelected() {}
        });
    }

    private void showMessageDialog(String category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sample Messages - " + category);

        List<String> samples = messageSamples.getOrDefault(category, new ArrayList<>());
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < Math.min(samples.size(), 5); i++) {
            message.append("• ").append(samples.get(i)).append("\n\n");
        }

        builder.setMessage(message.toString().isEmpty() ? "No samples available." : message.toString());
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private void animatePulse(View view) {
        AlphaAnimation pulse = new AlphaAnimation(0.5f, 1.0f);
        pulse.setDuration(600);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setRepeatCount(5);
        view.startAnimation(pulse);
    }

    private void rotateTipBanner() {
        tipBanner.setText(tips[tipIndex]);
        tipIndex = (tipIndex + 1) % tips.length;

        AlphaAnimation fade = new AlphaAnimation(0.0f, 1.0f);
        fade.setDuration(500);
        tipBanner.startAnimation(fade);

        handler.postDelayed(this::rotateTipBanner, 3000);
    }
}