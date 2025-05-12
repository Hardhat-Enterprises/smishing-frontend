package com.example.smishingdetectionapp.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.NewsActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private Spinner monthSpinner;
    private JSONObject analyticsJson;

    private TextView tvTotalAttacks, tvSuccess, tvFailed, tvUniqueSenders, tvAvgContentLength;
    private BarChart daywiseChart, stackedBarChart;
    private PieChart categoryChart;
    private LineChart weeklyTrendChart;
    private RadarChart severityRadarChart;

    private static final String[] WEEK_DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static final String[] SEVERITY_LABELS = {"Low", "Medium", "High", "Critical"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 1️⃣ Find all views
        monthSpinner = findViewById(R.id.spinner_month);
        tvTotalAttacks = findViewById(R.id.tvTotalAttacks);
        tvSuccess = findViewById(R.id.tvSuccess);
        tvFailed = findViewById(R.id.tvFailed);
        tvUniqueSenders = findViewById(R.id.tvUniqueSenders);
        tvAvgContentLength = findViewById(R.id.tvAvgContentLength);

        daywiseChart = findViewById(R.id.daywiseChart);
        categoryChart = findViewById(R.id.categoryChart);
        weeklyTrendChart = findViewById(R.id.weeklyTrendChart);
        stackedBarChart = findViewById(R.id.stackedBarChart);
        severityRadarChart = findViewById(R.id.severityRadarChart);

        // 2️⃣ Load our JSON asset
        loadAnalyticsJson();

        // 3️⃣ Set up month‐selector
        setupMonthSpinner();
    }

    private void loadAnalyticsJson() {
        try (InputStream is = getAssets().open("analytics_data.json")) {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String json = new String(buffer, StandardCharsets.UTF_8);
            analyticsJson = new JSONObject(json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            analyticsJson = new JSONObject();  // fallback empty
        }
    }

    private void setupMonthSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.months_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAnalyticsData(position);

                daywiseChart.invalidate();
                categoryChart.invalidate();
                weeklyTrendChart.invalidate();
                stackedBarChart.invalidate();
                severityRadarChart.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // initialize to current month
        int currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
        monthSpinner.setSelection(currentMonth, false);
        loadAnalyticsData(currentMonth);
    }

    private void loadAnalyticsData(int monthIndex) {
        try {
            JSONObject m = analyticsJson.getJSONObject(String.valueOf(monthIndex));

            // — Stats panel —
            int totalAttacks = m.getInt("totalAttacks");
            int successfulAttacks = m.getInt("successfulAttacks");
            int failedAttacks = totalAttacks - successfulAttacks;
            int uniqueSenders = m.getInt("uniqueSenders");
            double avgLength = m.getDouble("avgMessageLength");

            // text colors (optional)
            tvTotalAttacks.setTextColor(ContextCompat.getColor(this, R.color.navy_blue));
            tvSuccess.setTextColor(ContextCompat.getColor(this, R.color.blue_grotto));
            tvFailed.setTextColor(ContextCompat.getColor(this, R.color.red));
            tvUniqueSenders.setTextColor(ContextCompat.getColor(this, R.color.blue_green));
            tvAvgContentLength.setTextColor(ContextCompat.getColor(this, R.color.grey));

            tvTotalAttacks.setText(String.valueOf(totalAttacks));
            tvSuccess.setText(String.valueOf(successfulAttacks));
            tvFailed.setText(String.valueOf(failedAttacks));
            tvUniqueSenders.setText(String.valueOf(uniqueSenders));
            tvAvgContentLength.setText(String.format("%.1f", avgLength));

            // — 1. Day-wise Bar Chart —
            JSONArray dayArr = m.getJSONArray("daywiseData");
            List<BarEntry> barEntries = new ArrayList<>();
            for (int i = 0; i < dayArr.length(); i++) {
                barEntries.add(new BarEntry(i, (float) dayArr.getDouble(i)));
            }
            BarDataSet daySet = new BarDataSet(barEntries, "Attacks by Day");
            daySet.setColors(
                    ContextCompat.getColor(this, R.color.blue_grotto),
                    ContextCompat.getColor(this, R.color.blue_green)
            );
            daySet.setValueTextColor(ContextCompat.getColor(this, R.color.white));
            daywiseChart.setData(new BarData(daySet));
            daywiseChart.getDescription().setEnabled(false);
            daywiseChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
            XAxis xDay = daywiseChart.getXAxis();
            xDay.setValueFormatter(new IndexAxisValueFormatter(WEEK_DAYS));
            xDay.setPosition(XAxis.XAxisPosition.BOTTOM);
            daywiseChart.animateY(600);

            // — 2. Attack Categories Pie Chart —
            JSONObject catObj = m.getJSONObject("categoryData");
            List<PieEntry> pieEntries = new ArrayList<>();
            Iterator<String> keys = catObj.keys();
            while (keys.hasNext()) {
                String k = keys.next();
                pieEntries.add(new PieEntry((float) catObj.getDouble(k), k));
            }
            PieDataSet pieSet = new PieDataSet(pieEntries, "");
            pieSet.setSliceSpace(3f);
            pieSet.setColors(
                    ContextCompat.getColor(this, R.color.chart_phishing),
                    ContextCompat.getColor(this, R.color.chart_smishing),
                    ContextCompat.getColor(this, R.color.chart_vishing),
                    ContextCompat.getColor(this, R.color.chart_malware),
                    ContextCompat.getColor(this, R.color.chart_pharming)
            );
            PieData pieData = new PieData(pieSet);
            pieData.setValueTextSize(12f);
            pieData.setValueTextColor(ContextCompat.getColor(this, R.color.white));
            pieData.setValueFormatter(new PercentFormatter(categoryChart));
            categoryChart.setUsePercentValues(true);
            categoryChart.setData(pieData);
            categoryChart.getDescription().setEnabled(false);
            categoryChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
            categoryChart.animateY(800);

            // — 3. Weekly Trend Line Chart —
            JSONArray weekArr = m.getJSONArray("weeklyTrendData");
            List<Entry> lineEntries = new ArrayList<>();
            for (int i = 0; i < weekArr.length(); i++) {
                lineEntries.add(new Entry(i, (float) weekArr.getDouble(i)));
            }
            LineDataSet lineSet = new LineDataSet(lineEntries, "Attacks This Week");
            lineSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineSet.setDrawFilled(true);
            lineSet.setDrawCircles(true);
            lineSet.setCircleColor(ContextCompat.getColor(this, R.color.blue_green));
            weeklyTrendChart.setData(new LineData(lineSet));
            weeklyTrendChart.getDescription().setEnabled(false);
            weeklyTrendChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
            XAxis xLine = weeklyTrendChart.getXAxis();
            xLine.setValueFormatter(new IndexAxisValueFormatter(WEEK_DAYS));
            xLine.setPosition(XAxis.XAxisPosition.BOTTOM);
            weeklyTrendChart.animateX(800);

            // — 4. Success vs Failed Stacked Bar Chart —
            JSONArray sfArr = m.getJSONArray("successFailedData");
            List<BarEntry> stackEntries = new ArrayList<>();
            for (int i = 0; i < sfArr.length(); i++) {
                JSONArray row = sfArr.getJSONArray(i);
                stackEntries.add(new BarEntry(i, new float[]{
                        (float) row.getDouble(0),
                        (float) row.getDouble(1)
                }));
            }
            BarDataSet stackSet = new BarDataSet(stackEntries, "");
            stackSet.setStackLabels(new String[]{"Success", "Failed"});
            stackSet.setColors(
                    ContextCompat.getColor(this, R.color.blue_green),
                    ContextCompat.getColor(this, R.color.red)
            );
            stackSet.setValueTextColor(ContextCompat.getColor(this, R.color.white));
            stackedBarChart.setData(new BarData(stackSet));
            stackedBarChart.getDescription().setEnabled(false);
            stackedBarChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
            XAxis xStack = stackedBarChart.getXAxis();
            xStack.setValueFormatter(new IndexAxisValueFormatter(WEEK_DAYS));
            xStack.setPosition(XAxis.XAxisPosition.BOTTOM);
            stackedBarChart.animateY(800);

            // — 5. Severity Distribution Radar Chart —
            JSONArray sevArr = m.getJSONArray("severityData");
            List<RadarEntry> radarEntries = new ArrayList<>();
            for (int i = 0; i < sevArr.length(); i++) {
                radarEntries.add(new RadarEntry((float) sevArr.getDouble(i)));
            }
            RadarDataSet radarSet = new RadarDataSet(radarEntries, "");
            radarSet.setDrawFilled(true);
            severityRadarChart.setData(new RadarData(radarSet));
            severityRadarChart.getDescription().setEnabled(false);
            severityRadarChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
            severityRadarChart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return SEVERITY_LABELS[(int) value % SEVERITY_LABELS.length];
                }
            });
            severityRadarChart.animateXY(600, 600);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ⏹️ force redraw
        daywiseChart.invalidate();
        categoryChart.invalidate();
        weeklyTrendChart.invalidate();
        stackedBarChart.invalidate();
        severityRadarChart.invalidate();
    }
}

//public class DashboardActivity extends AppCompatActivity {
//    private static final String TAG = "DashboardActivity";
//
//    private Spinner monthSpinner;
//    private JSONObject analyticsJson;
//
//    private TextView tvTotalAttacks, tvSuccess, tvFailed, tvUniqueSenders, tvAvgContentLength;
//    private BarChart daywiseChart;
//    private PieChart categoryChart;
//    private LineChart weeklyTrendChart;
//    private BarChart stackedBarChart;
//    private RadarChart severityRadarChart;
//    private AnalyticsManager analyticsManager;
//
//    //  before loadAnalyticsData(), add:
//    private static final String[] WEEK_DAYS = new String[]{
//            "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dashboard);
//        Log.d(TAG, "onCreate called");
//
//        // Setup Bottom Navigation
//        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
//        bottomNav.setSelectedItemId(R.id.nav_dashboard);
//        bottomNav.setOnItemSelectedListener(item -> {
//            int id = item.getItemId();
//            if (id == R.id.nav_home) {
//                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
//                overridePendingTransition(0, 0);
//                finish();
//                return true;
//            } else if (id == R.id.nav_dashboard) {
//                return true;
//            } else if (id == R.id.nav_news) {
//                startActivity(new Intent(DashboardActivity.this, NewsActivity.class));
//                overridePendingTransition(0, 0);
//                finish();
//                return true;
//            } else if (id == R.id.nav_settings) {
//                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
//                overridePendingTransition(0, 0);
//                finish();
//                return true;
//            }
//            return false;
//        });
//
//        // Initialize AnalyticsManager
//        analyticsManager = new AnalyticsManager(this);
//        Log.d(TAG, "AnalyticsManager initialized");
//
//        // Bind UI elements
//        tvTotalAttacks = findViewById(R.id.tvTotalAttacks);
//        tvSuccess = findViewById(R.id.tvSuccess);
//        tvFailed = findViewById(R.id.tvFailed);
//        tvUniqueSenders = findViewById(R.id.tvUniqueSenders);
//        tvAvgContentLength = findViewById(R.id.tvAvgContentLength);
//        daywiseChart = findViewById(R.id.daywiseChart);
//        categoryChart = findViewById(R.id.categoryChart);
//        weeklyTrendChart = findViewById(R.id.weeklyTrendChart);
//        stackedBarChart = findViewById(R.id.stackedBarChart);
//        severityRadarChart = findViewById(R.id.severityRadarChart);
//
//        monthSpinner = findViewById(R.id.spinner_month);
//
//        // Load and display analytics data
//        // 4️⃣ Start on the current month
//        int currentMonthIndex = Calendar.getInstance().get(Calendar.MONTH);
//        loadAnalyticsJson();
//
//        initializeSpinnerUIOnCreate(currentMonthIndex);
//        loadAnalyticsData(currentMonthIndex);
//    }
//
//    private void loadAnalyticsJson() {
//        try (InputStream is = getAssets().open("analytics_data.json")) {
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            String json = new String(buffer, StandardCharsets.UTF_8);
//            analyticsJson = new JSONObject(json);
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//            analyticsJson = new JSONObject();
//        }
//    }
//
//
//    private void initializeSpinnerUIOnCreate(int currentMonthIndex) {
//        // 1️⃣ Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        // 3️⃣ Set up the month‐Spinner
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.months_array,
//                android.R.layout.simple_spinner_item
//        );
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        monthSpinner.setAdapter(adapter);
//        monthSpinner.setSelection(currentMonthIndex, false);
//
//        // 5️⃣ Listen for changes
//        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // position: 0 = January, …, 11 = December
//                loadAnalyticsData(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) { }
//        });
//    }
//
//    private void loadAnalyticsData(int monthIndex) {
//        // — Stats panel —
//        int totalAttacks = 134;       // demo totals
//        int successfulAttacks = 98;
//        int failedAttacks = 36;
//        int uniqueSenders = 52;
//        double avgLength = 142.8;
//
//        // Text colors
//        tvTotalAttacks.setTextColor(ContextCompat.getColor(this, R.color.navy_blue));
//        tvSuccess.setTextColor(ContextCompat.getColor(this, R.color.blue_grotto));
//        tvFailed.setTextColor(ContextCompat.getColor(this, R.color.red));
//        tvUniqueSenders.setTextColor(ContextCompat.getColor(this, R.color.blue_green));
//        tvAvgContentLength.setTextColor(ContextCompat.getColor(this, R.color.grey));
//
//        tvTotalAttacks.setText(String.valueOf(totalAttacks));
//        tvSuccess.setText(String.valueOf(successfulAttacks));
//        tvFailed.setText(String.valueOf(failedAttacks));
//        tvUniqueSenders.setText(String.valueOf(uniqueSenders));
//        tvAvgContentLength.setText(String.format("%.1f", avgLength));
//
//        // — 1. Day-wise Bar Chart (7 days) —
//        List<BarEntry> barEntries = new ArrayList<>();
//        float[] dayData = {12f, 18f, 15f, 22f, 17f, 25f, 20f};
//        for (int i = 0; i < dayData.length; i++) {
//            barEntries.add(new BarEntry(i, dayData[i]));
//        }
//        BarDataSet daySet = new BarDataSet(barEntries, "Attacks by Day");
//        daySet.setColors(
//                ContextCompat.getColor(this, R.color.blue_grotto),
//                ContextCompat.getColor(this, R.color.blue_green)
//        );
//        daySet.setValueTextColor(ContextCompat.getColor(this, R.color.white));
//        BarData dayDataObj = new BarData(daySet);
//        daywiseChart.setData(dayDataObj);
////        daywiseChart.setBackgroundColor(ContextCompat.getColor(this, R.color.azure));
//        daywiseChart.getDescription().setEnabled(false);
//        daywiseChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
//        daywiseChart.animateY(600);
//
//        XAxis xDayChart = daywiseChart.getXAxis();
//        xDayChart.setValueFormatter(new IndexAxisValueFormatter(WEEK_DAYS));
//        xDayChart.setGranularity(1f);
//        xDayChart.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xDayChart.setDrawGridLines(false);
//
//        // — 2. Attack Categories Pie Chart —
//        List<PieEntry> pieEntries = new ArrayList<>();
//        pieEntries.add(new PieEntry(50f, "Phishing"));
//        pieEntries.add(new PieEntry(30f, "Smishing"));
//        pieEntries.add(new PieEntry(20f, "Vishing"));
//        pieEntries.add(new PieEntry(15f, "Malware"));
//        pieEntries.add(new PieEntry(10f, "Pharming"));
//        PieDataSet pieSet = new PieDataSet(pieEntries, "");
//        pieSet.setSliceSpace(3f);
//        pieSet.setColors(
//                ContextCompat.getColor(this, R.color.chart_phishing),
//                ContextCompat.getColor(this, R.color.chart_smishing),
//                ContextCompat.getColor(this, R.color.chart_vishing),
//                ContextCompat.getColor(this, R.color.chart_malware),
//                ContextCompat.getColor(this, R.color.chart_pharming)
//        );
//        PieData pieData = new PieData(pieSet);
//        pieData.setValueTextSize(12f);
//        pieData.setValueTextColor(ContextCompat.getColor(this, R.color.white));
//        pieData.setValueFormatter(new PercentFormatter(categoryChart));
//        categoryChart.setUsePercentValues(true);
//        categoryChart.setData(pieData);
////        categoryChart.setBackgroundColor(ContextCompat.getColor(this, R.color.baby_blue));
//        categoryChart.getDescription().setEnabled(false);
//        categoryChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        categoryChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
//        categoryChart.animateY(800);
//
//        // — 3. Weekly Trend Line Chart —
//        List<Entry> lineEntries = new ArrayList<>();
//        float[] weekData = {10f, 14f, 18f, 22f, 19f, 24f, 28f};
//        for (int i = 0; i < weekData.length; i++) {
//            lineEntries.add(new Entry(i, weekData[i]));
//        }
//        LineDataSet lineSet = new LineDataSet(lineEntries, "Attacks This Week");
//        lineSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        lineSet.setDrawFilled(true);
////        lineSet.setFillColor(ContextCompat.getColor(this, R.color.blue_grotto));
//        lineSet.setDrawCircles(true);
//        lineSet.setCircleColor(ContextCompat.getColor(this, R.color.blue_green));
//        LineData lineData = new LineData(lineSet);
//        weeklyTrendChart.setData(lineData);
//        weeklyTrendChart.getDescription().setEnabled(false);
//        weeklyTrendChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
//        weeklyTrendChart.animateX(800);
//
//        XAxis xLine = weeklyTrendChart.getXAxis();
//        xLine.setValueFormatter(new IndexAxisValueFormatter(WEEK_DAYS));
//        xLine.setGranularity(1f);
//        xLine.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xLine.setDrawGridLines(false);
//
//        // — 4. Success vs Failed Stacked Bar Chart —
//        List<BarEntry> stackEntries = new ArrayList<>();
//        float[][] sf = {{8f, 2f}, {12f, 3f}, {10f, 5f}, {14f, 6f}, {16f, 4f}, {18f, 7f}, {20f, 8f}};
//        for (int i = 0; i < sf.length; i++) {
//            stackEntries.add(new BarEntry(i, sf[i]));
//        }
//        BarDataSet stackSet = new BarDataSet(stackEntries, "");
//        stackSet.setStackLabels(new String[]{"Success", "Failed"});
//        stackSet.setColors(
//                ContextCompat.getColor(this, R.color.blue_green),
//                ContextCompat.getColor(this, R.color.red)
//        );
//        stackSet.setValueTextColor(ContextCompat.getColor(this, R.color.white));
//        BarData stackData = new BarData(stackSet);
//        stackedBarChart.setData(stackData);
//        stackedBarChart.getDescription().setEnabled(false);
//        stackedBarChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
//        stackedBarChart.animateY(800);
//
//        XAxis xStack = stackedBarChart.getXAxis();
//        xStack.setValueFormatter(new IndexAxisValueFormatter(WEEK_DAYS));
//        xStack.setGranularity(1f);
//        xStack.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xStack.setDrawGridLines(false);
//
//        // — 5. Severity Distribution Radar Chart —
//        List<RadarEntry> radarEntries = new ArrayList<>();
//        float[] sev = {35f, 45f, 12f, 8f}; // Low, Med, High, Critical
//        for (float v : sev) radarEntries.add(new RadarEntry(v));
//        RadarDataSet radarSet = new RadarDataSet(radarEntries, "");
//        radarSet.setDrawFilled(true);
//        radarSet.setFillColor(ContextCompat.getColor(this, R.color.chart_smishing));
//        RadarData radarData = new RadarData(radarSet);
//        severityRadarChart.setData(radarData);
//        String[] labels = {"Low", "Medium", "High", "Critical"};
//        severityRadarChart.getXAxis().setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                return labels[(int) value % labels.length];
//            }
//        });
//        severityRadarChart.getDescription().setEnabled(false);
//        severityRadarChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
//        severityRadarChart.animateXY(600, 600);
//    }
//
//
//}


