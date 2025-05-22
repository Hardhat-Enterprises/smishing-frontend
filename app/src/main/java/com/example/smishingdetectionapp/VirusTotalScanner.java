package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.*;

import java.io.IOException;

public class VirusTotalScanner extends AppCompatActivity {

    private static final String VT_API_KEY = "e02aa771b39881b01a39696594554735363695dc088e53240bbb74c864be75cc";
    private static final String GSB_API_KEY = "AIzaSyBlU6Oza2_spFW7apJ3Nw_QMQZ3hUNgU2k";

    private EditText domainInput;
    private Button scanButton, vtDetailsButton, gsbDetailsButton;
    private ProgressBar loadingBar;
    private TextView verdictText;
    private ImageView verdictIcon;
    private ImageButton backButton;

    private String vtVerdict = "Unknown";
    private String gsbVerdict = "Unknown";
    private String heuristicVerdict = "Unknown";

    private String vtJson = "";
    private String gsbJson = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virustotal_scanner);

        initUI();

        scanButton.setOnClickListener(v -> startScan());
        vtDetailsButton.setOnClickListener(v -> showDetails("VirusTotal Report", vtJson));
        gsbDetailsButton.setOnClickListener(v -> showDetails("Google Safe Browsing Report", gsbJson));
        backButton.setOnClickListener(v -> finish());
    }

    private void initUI() {
        domainInput = findViewById(R.id.editTextDomain);
        scanButton = findViewById(R.id.buttonScan);
        vtDetailsButton = findViewById(R.id.buttonVTDetails);
        gsbDetailsButton = findViewById(R.id.buttonGSBDetails);

        loadingBar = findViewById(R.id.progressBar);
        verdictText = findViewById(R.id.verdictTextView);
        verdictIcon = findViewById(R.id.verdictIcon);
        backButton = findViewById(R.id.backbuton);

        loadingBar.setVisibility(View.GONE);
        vtDetailsButton.setVisibility(View.GONE);
        gsbDetailsButton.setVisibility(View.GONE);
    }

    private void startScan() {
        String domain = domainInput.getText().toString().trim();

        if (domain.isEmpty()) {
            Toast.makeText(this, "Please enter a domain", Toast.LENGTH_SHORT).show();
            return;
        }

        // Basic domain format check (no protocol, only domain)
        if (!domain.matches("^(?!-)[A-Za-z0-9-]+(\\.[A-Za-z]{2,})+$")) {
            Toast.makeText(this, "Invalid domain format entered", Toast.LENGTH_SHORT).show();
            return;
        }

        verdictText.setText("");
        verdictIcon.setImageDrawable(null);
        loadingBar.setVisibility(View.VISIBLE);
        scanButton.setEnabled(false);

        vtDetailsButton.setVisibility(View.GONE);
        gsbDetailsButton.setVisibility(View.GONE);

        new ScanTask().execute(domain);
    }


    private class ScanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String domain = params[0];
            OkHttpClient client = new OkHttpClient();
            StringBuilder errors = new StringBuilder();

            // Heuristic Analysis
            heuristicVerdict = analyzeHeuristically(domain);

            // VirusTotal API Call
            Request vtRequest = new Request.Builder()
                    .url("https://www.virustotal.com/api/v3/domains/" + domain)
                    .get()
                    .addHeader("x-apikey", VT_API_KEY)
                    .build();

            try (Response response = client.newCall(vtRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    vtJson = response.body().string();
                    parseVirusTotal(vtJson);
                } else {
                    errors.append("VirusTotal error: ").append(response.code()).append("\n");
                    vtJson = "";
                    vtVerdict = "Unknown";
                }
            } catch (IOException e) {
                errors.append("VirusTotal failed: ").append(e.getMessage()).append("\n");
                vtVerdict = "Unknown";
            }

            // Google Safe Browsing API Call
            String gsbPayload = "{ \"client\": {\"clientId\": \"smishingApp\", \"clientVersion\": \"1.0\"}, " +
                    "\"threatInfo\": { \"threatTypes\": [\"MALWARE\", \"SOCIAL_ENGINEERING\"], " +
                    "\"platformTypes\": [\"ANY_PLATFORM\"], " +
                    "\"threatEntryTypes\": [\"URL\"], " +
                    "\"threatEntries\": [{\"url\": \"http://" + domain + "\"}] } }";

            Request gsbRequest = new Request.Builder()
                    .url("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + GSB_API_KEY)
                    .post(RequestBody.create(gsbPayload, MediaType.parse("application/json")))
                    .build();

            try (Response response = client.newCall(gsbRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    gsbJson = response.body().string();
                    parseGSB(gsbJson);
                } else {
                    errors.append("GSB error: ").append(response.code()).append("\n");
                    gsbJson = "";
                    gsbVerdict = "Unknown";
                }
            } catch (IOException e) {
                errors.append("GSB failed: ").append(e.getMessage()).append("\n");
                gsbVerdict = "Unknown";
            }

            return errors.toString().trim();
        }

        @Override
        protected void onPostExecute(String errors) {
            loadingBar.setVisibility(View.GONE);
            scanButton.setEnabled(true);

            if (!errors.isEmpty()) {
                Toast.makeText(VirusTotalScanner.this, errors, Toast.LENGTH_LONG).show();
            }

            String finalVerdict;
            if ("Malicious".equals(vtVerdict) || "Malicious".equals(gsbVerdict) || "Malicious".equals(heuristicVerdict)) {
                finalVerdict = "⚠️ Malicious website detected!";
                verdictIcon.setImageResource(android.R.drawable.ic_dialog_alert);
            } else if ("Suspicious".equals(vtVerdict) || "Suspicious".equals(heuristicVerdict)) {
                finalVerdict = "⚠️ Website may be suspicious.";
                verdictIcon.setImageResource(android.R.drawable.ic_dialog_info);
            } else if ("Safe".equals(vtVerdict) && "Safe".equals(gsbVerdict) && "Safe".equals(heuristicVerdict)) {
                finalVerdict = "✅ Website appears safe.";
                verdictIcon.setImageResource(android.R.drawable.ic_menu_info_details);
            } else {
                finalVerdict = "❓ Verdict unknown.";
                verdictIcon.setImageDrawable(null);
            }

            verdictText.setText(finalVerdict);

            vtDetailsButton.setVisibility(vtJson.isEmpty() ? View.GONE : View.VISIBLE);
            gsbDetailsButton.setVisibility(gsbJson.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void parseVirusTotal(String json) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonObject stats = jsonObject.getAsJsonObject("data")
                    .getAsJsonObject("attributes")
                    .getAsJsonObject("last_analysis_stats");

            int malicious = stats.get("malicious").getAsInt();
            int suspicious = stats.get("suspicious").getAsInt();

            if (malicious >= 5) vtVerdict = "Malicious";
            else if (malicious > 0 || suspicious > 0) vtVerdict = "Suspicious";
            else vtVerdict = "Safe";
        } catch (Exception e) {
            vtVerdict = "Unknown";
        }
    }

    private void parseGSB(String json) {
        gsbVerdict = json.contains("matches") ? "Malicious" : "Safe";
    }

    private void showDetails(String title, String report) {
        Intent intent = new Intent(this, ReportDetailsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("report", report);
        startActivity(intent);
    }

    private String analyzeHeuristically(String domain) {
        String lowerDomain = domain.toLowerCase();

        String[] suspiciousKeywords = {"login", "verify", "bank", "update", "secure", "account", "paypal", "free", "win", "gift", "offer"};
        for (String keyword : suspiciousKeywords) {
            if (lowerDomain.contains(keyword)) {
                return "Malicious";
            }
        }

        if (lowerDomain.length() < 8 || lowerDomain.length() > 50) {
            return "Suspicious";
        }

        int hyphenCount = 0;
        int digitCount = 0;
        for (char c : lowerDomain.toCharArray()) {
            if (c == '-') hyphenCount++;
            if (Character.isDigit(c)) digitCount++;
        }
        if (hyphenCount > 3 || digitCount > 5) {
            return "Suspicious";
        }

        String[] suspiciousTLDs = {".xyz", ".top", ".info", ".club", ".click", ".tk"};
        for (String tld : suspiciousTLDs) {
            if (lowerDomain.endsWith(tld)) {
                return "Malicious";
            }
        }

        return "Safe";
    }
}
