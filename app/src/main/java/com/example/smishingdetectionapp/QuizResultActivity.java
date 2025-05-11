package com.example.smishingdetectionapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class QuizResultActivity extends AppCompatActivity {

    private Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);

        // Get data from Intent
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        ArrayList<String> questions = getIntent().getStringArrayListExtra("questions");
        ArrayList<String[]> options = (ArrayList<String[]>) getIntent().getSerializableExtra("options");
        ArrayList<Integer> userAnswers = getIntent().getIntegerArrayListExtra("userAnswers");
        ArrayList<Integer> correctAnswers = getIntent().getIntegerArrayListExtra("correctAnswers");
        ArrayList<Integer> timeSpent = getIntent().getIntegerArrayListExtra("timeSpent");

        // Display score
        TextView scoreTextView = findViewById(R.id.scoreText);
        scoreTextView.setText("Score: " + score + " / " + totalQuestions);

        // Display question review
        LinearLayout historyLayout = findViewById(R.id.historyLayout);
        if (questions != null && options != null && userAnswers != null && correctAnswers != null && timeSpent != null) {
            for (int i = 0; i < questions.size(); i++) {
                addQuestionReview(historyLayout, i, questions.get(i), options.get(i), userAnswers.get(i), correctAnswers.get(i), timeSpent.get(i));
            }
        }

        // Home button
        Button homeButton = findViewById(R.id.backToHomeButton);
        homeButton.setOnClickListener(v -> finish());

        // Share button
        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> captureAndShareScreenshot());
    }

    private void addQuestionReview(LinearLayout layout, int index, String question, String[] choices, int userAnswer, int correctAnswer, int timeTaken) {
        // Question text
        TextView questionView = new TextView(this);
        questionView.setText((index + 1) + ". " + question);
        questionView.setTextSize(16);
        layout.addView(questionView);

        // User's answer
        TextView userAnswerView = new TextView(this);
        if (userAnswer != correctAnswer) {
            userAnswerView.setText("  Your Answer: " +
                    (userAnswer != -1 ? choices[userAnswer] : "No answer selected"));
            userAnswerView.setTextColor(0xFFFF0000); // Red
        } else {
            userAnswerView.setText("  Your Answer: Correct");
            userAnswerView.setTextColor(0xFF228B22); // Green
        }
        layout.addView(userAnswerView);

        // Correct answer (only if wrong)
        if (userAnswer != correctAnswer) {
            TextView correctAnswerView = new TextView(this);
            correctAnswerView.setText("  Correct Answer: " + choices[correctAnswer]);
            correctAnswerView.setTextColor(0xFF228B22); // Green
            layout.addView(correctAnswerView);
        }

        // Time taken
        TextView timeView = new TextView(this);
        timeView.setText("  Time Taken: " + timeTaken + " sec");
        timeView.setTextSize(14);
        layout.addView(timeView);

        // Separator
        TextView separator = new TextView(this);
        separator.setText("-----------------------------");
        layout.addView(separator);
    }

    private void captureAndShareScreenshot() {
        Bitmap screenshot = captureScreen();
        if (screenshot != null) {
            try {
                File screenshotFile = saveScreenshot(screenshot);
                shareScreenshot(screenshotFile);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving screenshot", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Screenshot failed", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap captureScreen() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private File saveScreenshot(Bitmap bitmap) throws IOException {
        File screenshotsDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Screenshots");
        if (!screenshotsDir.exists()) {
            screenshotsDir.mkdirs();
        }

        File screenshotFile = new File(screenshotsDir, "quiz_result.png");
        try (FileOutputStream fos = new FileOutputStream(screenshotFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        return screenshotFile;
    }

    private void shareScreenshot(File screenshotFile) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", screenshotFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Quiz Result"));
    }
}
