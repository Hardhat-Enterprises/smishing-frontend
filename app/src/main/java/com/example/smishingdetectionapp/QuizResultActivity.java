package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class QuizResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);


        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        ArrayList<String> questions = getIntent().getStringArrayListExtra("questions");
        ArrayList<String[]> options = (ArrayList<String[]>) getIntent().getSerializableExtra("options");
        ArrayList<Integer> userAnswers = getIntent().getIntegerArrayListExtra("userAnswers");
        ArrayList<Integer> correctAnswers = getIntent().getIntegerArrayListExtra("correctAnswers");


        TextView scoreTextView = findViewById(R.id.scoreText);
        scoreTextView.setText("Score: " + score + " / " + totalQuestions);


        LinearLayout historyLayout = findViewById(R.id.historyLayout);
        for (int i = 0; i < questions.size(); i++) {

            TextView questionView = new TextView(this);
            questionView.setText((i + 1) + ". " + questions.get(i));
            questionView.setTextSize(16);
            historyLayout.addView(questionView);


            int correctAnswerIndex = correctAnswers.get(i);
            String correctAnswerText = options.get(i)[correctAnswerIndex];


            int userAnswerIndex = userAnswers.get(i);
            TextView userAnswerView = new TextView(this);

            if (userAnswerIndex != correctAnswerIndex) {
                userAnswerView.setText("  Your Answer: " +
                        (userAnswerIndex != -1 ? options.get(i)[userAnswerIndex] : "No answer selected"));
                userAnswerView.setTextColor(0xFFFF0000);
            } else {
                userAnswerView.setText("  Your Answer: Correct");
                userAnswerView.setTextColor(0xFF228B22);
            }

            historyLayout.addView(userAnswerView);

            if (userAnswerIndex != correctAnswerIndex) {
                TextView correctAnswerView = new TextView(this);
                correctAnswerView.setText("  Correct Answer: " + correctAnswerText);
                correctAnswerView.setTextColor(0xFF228B22);
                historyLayout.addView(correctAnswerView);
            }


            TextView separator = new TextView(this);
            separator.setText("-----------------------------");
            historyLayout.addView(separator);
        }


        Button homeButton = findViewById(R.id.backToHomeButton);
        homeButton.setOnClickListener(v -> finish());
    }
}
