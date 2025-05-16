package com.example.smishingdetectionapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import android.widget.ImageButton;


public class VirusTotalScanner extends AppCompatActivity {

    private static final String VT_API_KEY = "e02aa771b39881b01a39696594554735363695dc088e53240bbb74c864be75cc";
    private static final String GSB_API_KEY = "AIzaSyBlU6Oza2_spFW7apJ3Nw_QMQZ3hUNgU2k"; // TODO: Replace with your key

    private EditText editTextDomain;
    private Button buttonScan, buttonVTDetails, buttonGSBDetails;
    private ProgressBar progressBar;
    private TextView verdictTextView;
    private ImageView verdictIcon;

    private String vtVerdict = "Unknown";
    private String gsbVerdict = "Unknown";

    // Store full JSON reports for details view
    private String vtFullJson = "";
    private String gsbFullJson = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virustotal_scanner);

        editTextDomain = findViewById(R.id.editTextDomain);
        buttonScan = findViewById(R.id.buttonScan);
        buttonVTDetails = findViewById(R.id.buttonVTDetails);
        buttonGSBDetails = findViewById(R.id.buttonGSBDetails);
        progressBar = findViewById(R.id.progressBar);
        verdictTextView = findViewById(R.id.verdictTextView);
        verdictIcon = findViewById(R.id.verdictIcon);

        progressBar.setVisibility(View.GONE);
        buttonVTDetails.setVisibility(View.GONE);
        buttonGSBDetails.setVisibility(View.GONE);

        buttonScan.setOnClickListener(v -> {
            String domain = editTextDomain.getText().toString().trim();
            if (domain.isEmpty()) {
                Toast.makeText(this, "Please enter a domain", Toast.LENGTH_SHORT).show();
                return;
            }

            verdictTextView.setText("");
            verdictIcon.setImageDrawable(null);
            progressBar.setVisibility(View.VISIBLE);
            buttonScan.setEnabled(false);
            buttonVTDetails.setVisibility(View.GONE);
            buttonGSBDetails.setVisibility(View.GONE);

            new ScanTask().execute(domain);
        });
        ImageButton backButton = findViewById(R.id.backbuton);
        backButton.setOnClickListener(v -> finish());


        buttonVTDetails.setOnClickListener(v -> showDetails("VirusTotal Report", vtFullJson));
        buttonGSBDetails.setOnClickListener(v -> showDetails("Google Safe Browsing Report", gsbFullJson));
    }

    private void showDetails(String title, String fullJsonReport) {
        Intent intent = new Intent(this, ReportDetailsActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("report", fullJsonReport);
        startActivity(intent);
    }

    private class ScanTask extends AsyncTask<String, Void, String> {

        private String error = "";

        @Override
        protected String doInBackground(String... domains) {
            String domain = domains[0];
            OkHttpClient client = new OkHttpClient();

            // VirusTotal request
            Request vtRequest = new Request.Builder()
                    .url("https://www.virustotal.com/api/v3/domains/" + domain)
                    .get()
                    .addHeader("x-apikey", VT_API_KEY)
                    .build();

            try (Response response = client.newCall(vtRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    vtFullJson = response.body().string();
                    parseVirusTotalResult(vtFullJson);  // Update vtVerdict internally
                } else {
                    error += "VirusTotal error: " + response.code() + "\n";
                    vtFullJson = "";
                    vtVerdict = "Unknown";
                }
            } catch (IOException e) {
                error += "VirusTotal failed: " + e.getMessage() + "\n";
                vtFullJson = "";
                vtVerdict = "Unknown";
            }

            // Google Safe Browsing request
            String jsonPayload = "{ \"client\": {\"clientId\": \"smishingApp\", \"clientVersion\": \"1.0\"}, " +
                    "\"threatInfo\": { \"threatTypes\": [\"MALWARE\", \"SOCIAL_ENGINEERING\"], " +
                    "\"platformTypes\": [\"ANY_PLATFORM\"], " +
                    "\"threatEntryTypes\": [\"URL\"], " +
                    "\"threatEntries\": [{\"url\": \"http://" + domain + "\"}] } }";

            Request gsbRequest = new Request.Builder()
                    .url("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + GSB_API_KEY)
                    .post(RequestBody.create(jsonPayload, MediaType.parse("application/json")))
                    .build();

            try (Response response = client.newCall(gsbRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    gsbFullJson = response.body().string();
                    parseGSBResult(gsbFullJson); // Update gsbVerdict internally
                } else {
                    error += "Google Safe Browsing error: " + response.code() + "\n";
                    gsbFullJson = "";
                    gsbVerdict = "Unknown";
                }
            } catch (IOException e) {
                error += "Google Safe Browsing failed: " + e.getMessage() + "\n";
                gsbFullJson = "";
                gsbVerdict = "Unknown";
            }

            return error;
        }

        @Override
        protected void onPostExecute(String error) {
            progressBar.setVisibility(View.GONE);
            buttonScan.setEnabled(true);

            if (!error.isEmpty()) {
                Toast.makeText(VirusTotalScanner.this, error.trim(), Toast.LENGTH_LONG).show();
            }

            String finalVerdict;
            if ("Malicious".equals(vtVerdict) || "Malicious".equals(gsbVerdict)) {
                finalVerdict = "⚠️ Malicious website detected!";
                verdictIcon.setImageResource(android.R.drawable.ic_dialog_alert);
            } else if ("Suspicious".equals(vtVerdict)) {
                finalVerdict = "⚠️ Website may be suspicious.";
                verdictIcon.setImageResource(android.R.drawable.ic_dialog_info);
            } else if ("Safe".equals(vtVerdict) && "Safe".equals(gsbVerdict)) {
                finalVerdict = "✅ Website appears safe.";
                verdictIcon.setImageResource(android.R.drawable.ic_menu_info_details);
            } else {
                finalVerdict = "❓ Verdict unknown.";
                verdictIcon.setImageDrawable(null);
            }

            verdictTextView.setText(finalVerdict);

            // Show details buttons only if reports available
            buttonVTDetails.setVisibility(vtFullJson.isEmpty() ? View.GONE : View.VISIBLE);
            buttonGSBDetails.setVisibility(gsbFullJson.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void parseVirusTotalResult(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            JsonObject stats = obj.getAsJsonObject("data")
                    .getAsJsonObject("attributes")
                    .getAsJsonObject("last_analysis_stats");

            int malicious = stats.get("malicious").getAsInt();
            int suspicious = stats.get("suspicious").getAsInt();
            int harmless = stats.get("harmless").getAsInt();
            int undetected = stats.get("undetected").getAsInt();

            if (malicious >= 5) vtVerdict = "Malicious";
            else if (malicious > 0 || suspicious > 0) vtVerdict = "Suspicious";
            else vtVerdict = "Safe";

        } catch (Exception e) {
            vtVerdict = "Unknown";
        }
    }

    private void parseGSBResult(String json) {
        if (json.contains("matches")) {
            gsbVerdict = "Malicious";
        } else {
            gsbVerdict = "Safe";
        }
    }
}
