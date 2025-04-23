package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Random;

public class EducationActivity extends AppCompatActivity {
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        // ✅ 1. Security Tip of the Day
        String[] tips = {
                "Be cautious of links from unknown numbers.",
                "Never share OTPs with anyone.",
                "Look for suspicious language in messages.",
                "Verify sender details before clicking links.",
                "Ignore messages that create urgency or fear.",
                "Check URLs before opening — hover when possible.",
                "Don’t install apps from untrusted sources."
        };
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Toast.makeText(this, "📌 Tip of the Day: " + tips[dayOfWeek - 1], Toast.LENGTH_LONG).show();

        // ✅ 2. Track Streaks - Show badge on repeated use
        SharedPreferences prefs = getSharedPreferences("smishEduPrefs", MODE_PRIVATE);
        int visits = prefs.getInt("educationVisits", 0) + 1;
        prefs.edit().putInt("educationVisits", visits).apply();

        if (visits == 3) {
            Toast.makeText(this, "🎖️ You’ve visited 3 times! Keep it up, Smishing Rookie!", Toast.LENGTH_LONG).show();
        } else if (visits == 5) {
            Toast.makeText(this, "🏅 Level Up! You're a Smishing Explorer!", Toast.LENGTH_LONG).show();
        } else if (visits == 7) {
            Toast.makeText(this, "🚨 You're now a Smishing Slayer. Respect.", Toast.LENGTH_LONG).show();
        }

        // 🔙 Back Button
        ImageButton backButton = findViewById(R.id.education_back);
        backButton.setOnClickListener(v -> finish());

        // ▶️ YouTube Educational Video
        WebView youtubeWebView = findViewById(R.id.youtubeWebView);
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        youtubeWebView.setWebViewClient(new WebViewClient());
        youtubeWebView.loadUrl("https://www.youtube.com/embed/ZOZGQeG8avQ");

        // 🎡 3. Spin the Wheel with embedded "Smish or Safe?" mini-quiz
        Button quizButton = findViewById(R.id.quiz_button);
        quizButton.setOnClickListener(v -> {
            String[] spinTips = {
                    "Don't fall for 'You won a prize!' SMS messages.",
                    "Smishers pretend to be banks. Always verify.",
                    "Never share OTPs over text.",
                    "Ignore links that create urgency.",
                    "Check for grammar errors in texts.",
                    "Do not install apps from untrusted links.",
                    "Use built-in spam protection on your phone."
            };

            AlertDialog spinningDialog = new AlertDialog.Builder(EducationActivity.this)
                    .setTitle("🎡 Spinning...")
                    .setMessage("Get ready for your smishing tip!")
                    .setCancelable(false)
                    .create();

            spinningDialog.show();

            final int[] counter = {0};
            final int[] delay = {100};
            final Random rand = new Random();

            final Handler handler = new Handler();
            Runnable spinner = new Runnable() {
                @Override
                public void run() {
                    if (counter[0] < 15) {
                        int index = rand.nextInt(spinTips.length);
                        spinningDialog.setMessage(spinTips[index]);
                        counter[0]++;
                        delay[0] += 50;
                        handler.postDelayed(this, delay[0]);
                    } else {
                        spinningDialog.dismiss();
                        int finalIndex = rand.nextInt(spinTips.length);

                        new AlertDialog.Builder(EducationActivity.this)
                                .setTitle("🎯 Your Tip!")
                                .setMessage(spinTips[finalIndex])
                                .setPositiveButton("Cool!", (dialog, which) -> launchMiniGame())
                                .show();
                    }
                }
            };
            handler.post(spinner);
        });
    }

    // 🚀 MINI QUIZ: "Smish or Safe?"
    private void launchMiniGame() {
        String scenario = "You get a text saying 'Your bank account is suspended. Click this link to fix it.' Would you click it?";

        new AlertDialog.Builder(this)
                .setTitle("🤔 Smish or Safe?")
                .setMessage(scenario)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(this, "❌ Nope! That was a smishing attempt!", Toast.LENGTH_LONG).show();
                    offerFullQuiz();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    Toast.makeText(this, "✅ Smart move! Always verify such messages.", Toast.LENGTH_LONG).show();
                    offerFullQuiz();
                })
                .show();
    }

    // 🔁 Offer to Start Full Quiz
    private void offerFullQuiz() {
        new AlertDialog.Builder(this)
                .setTitle("🧠 Ready for More?")
                .setMessage("Want to take the full quiz now?")
                .setPositiveButton("Start Quiz", (dialog, which) -> {
                    Intent intent = new Intent(EducationActivity.this, QuizesActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("No thanks", null)
                .show();
    }
}
