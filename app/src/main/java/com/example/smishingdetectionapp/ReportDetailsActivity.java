package com.example.smishingdetectionapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ReportDetailsActivity extends AppCompatActivity {

    private LinearLayout reportContainer;
    private TextView textViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        reportContainer = findViewById(R.id.reportContainer);
        textViewTitle = findViewById(R.id.textViewTitle);
        ImageButton backButton = findViewById(R.id.BACK);

        backButton.setOnClickListener(v -> finish());

        String reportJson = getIntent().getStringExtra("report");
        String title = getIntent().getStringExtra("title");

        if (reportJson != null) {
            if (title != null && title.contains("VirusTotal")) {
                textViewTitle.setText("VirusTotal Report");
                formatVirusTotalReport(reportJson);
            } else if (title != null && title.contains("Google Safe Browsing")) {
                textViewTitle.setText("Google Safe Browsing Report");
                formatGSBReport(reportJson);
            } else {
                textViewTitle.setText("Unknown Report");
                addSimpleText("Unknown report format.");
            }
        } else {
            textViewTitle.setText("No Report Data");
            addSimpleText("No report data available.");
        }
    }

    private void addSimpleText(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTextColor(Color.DKGRAY);
        tv.setPadding(8, 8, 8, 8);
        reportContainer.addView(tv);
    }

    private void addSectionCard(String title, String content) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 12, 0, 12);
        card.setLayoutParams(cardParams);
        card.setRadius(12);
        card.setCardElevation(6);
        card.setUseCompatPadding(true);
        card.setContentPadding(20, 20, 20, 20);
        card.setCardBackgroundColor(Color.WHITE);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(Color.parseColor("#3F51B5"));  // Indigo color
        tvTitle.setPadding(0, 0, 0, 12);
        tvTitle.setGravity(Gravity.START);

        TextView tvContent = new TextView(this);
        tvContent.setText(content);
        tvContent.setTextSize(16);
        tvContent.setTextColor(Color.DKGRAY);

        container.addView(tvTitle);
        container.addView(tvContent);
        card.addView(container);
        reportContainer.addView(card);
    }

    private void formatVirusTotalReport(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            JsonObject data = obj.getAsJsonObject("data");
            JsonObject attributes = data.getAsJsonObject("attributes");

            addSectionCard("Domain", data.get("id").getAsString());

            addSectionCard("Reputation", String.valueOf(attributes.get("reputation").getAsInt()));

            if (attributes.has("whois_date")) {
                addSectionCard("WHOIS Date", String.valueOf(attributes.get("whois_date").getAsLong()));
            }

            if (attributes.has("categories")) {
                JsonObject categories = attributes.getAsJsonObject("categories");
                StringBuilder categoriesList = new StringBuilder();
                for (String key : categories.keySet()) {
                    categoriesList.append(key).append(": ").append(categories.get(key).getAsString()).append("\n");
                }
                addSectionCard("Categories", categoriesList.toString().trim());
            }

            if (attributes.has("last_analysis_results")) {
                JsonObject engines = attributes.getAsJsonObject("last_analysis_results");
                StringBuilder engineResults = new StringBuilder();
                for (String engineName : engines.keySet()) {
                    JsonObject engineResult = engines.getAsJsonObject(engineName);
                    String category = engineResult.get("category").getAsString();
                    String result = engineResult.get("result").isJsonNull() ? "clean" : engineResult.get("result").getAsString();
                    engineResults.append(engineName)
                            .append(": ")
                            .append(category)
                            .append(" (")
                            .append(result)
                            .append(")\n");
                }
                addSectionCard("Engine Results", engineResults.toString().trim());
            }
        } catch (Exception e) {
            addSimpleText("Error parsing VirusTotal report: " + e.getMessage());
        }
    }

    private void formatGSBReport(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            if (obj.has("matches")) {
                JsonArray matches = obj.getAsJsonArray("matches");
                for (JsonElement matchElem : matches) {
                    JsonObject match = matchElem.getAsJsonObject();
                    JsonObject threat = match.getAsJsonObject("threat");
                    String url = threat.get("url").getAsString();

                    StringBuilder threatDetails = new StringBuilder();
                    threatDetails.append("Threat URL: ").append(url).append("\n");

                    if (match.has("threatType")) {
                        threatDetails.append("Threat Type: ").append(match.get("threatType").getAsString()).append("\n");
                    }
                    if (match.has("platformType")) {
                        threatDetails.append("Platform: ").append(match.get("platformType").getAsString()).append("\n");
                    }
                    addSectionCard("Threat Detected", threatDetails.toString().trim());
                }
            } else {
                addSimpleText("Google Safe Browsing: No threats found.");
            }
        } catch (Exception e) {
            addSimpleText("Error parsing Google Safe Browsing report: " + e.getMessage());
        }
    }
}