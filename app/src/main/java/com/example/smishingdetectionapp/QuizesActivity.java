package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizesActivity extends AppCompatActivity {

    // Updated Question class with a new difficulty field
    class Question {
        String questionText;
        String[] options;
        int correctOptionIndex;
        String difficulty;  // Difficulty level (e.g., "Easy", "Medium", "Hard")

        Question(String questionText, String[] options, int correctOptionIndex, String difficulty) {
            this.questionText = questionText;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
            this.difficulty = difficulty;
        }
    }

    private List<Question> questionBank;
    private List<Integer> userAnswers;
    private int currentQuestionIndex = 0;
    private int score = 0;

    private TextView questionTextView;
    private RadioGroup optionsGroup;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);

        // Initialize the question bank with expanded questions and difficulty levels
        initializeQuestionBank();

        // Optional: Filter questions based on difficulty passed from the previous activity
        String selectedDifficulty = getIntent().getStringExtra("difficulty");
        if (selectedDifficulty != null && !selectedDifficulty.isEmpty()) {
            List<Question> filteredQuestions = new ArrayList<>();
            for (Question q : questionBank) {
                if (q.difficulty.equalsIgnoreCase(selectedDifficulty)) {
                    filteredQuestions.add(q);
                }
            }
            if (filteredQuestions.size() > 0) {
                questionBank = filteredQuestions;
            } else {
                Toast.makeText(this, "No questions found for selected difficulty. Using all questions.", Toast.LENGTH_SHORT).show();
            }
        }

        // Shuffle questions randomly
        Collections.shuffle(questionBank);

        // Initialize user answers with default value (-1 indicates no answer selected)
        userAnswers = new ArrayList<>(Collections.nCopies(questionBank.size(), -1));

        displayQuestion();

        nextButton.setOnClickListener(v -> {
            int selectedOptionId = optionsGroup.getCheckedRadioButtonId();
            if (selectedOptionId == -1) {
                Toast.makeText(this, "Please select an answer before proceeding!", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedOptionIndex = optionsGroup.indexOfChild(findViewById(selectedOptionId));
            userAnswers.set(currentQuestionIndex, selectedOptionIndex);

            // Increase score if the answer is correct
            if (selectedOptionIndex == questionBank.get(currentQuestionIndex).correctOptionIndex) {
                score++;
            }

            currentQuestionIndex++;
            if (currentQuestionIndex < questionBank.size()) {
                displayQuestion();
            } else {
                showResults();
            }
        });
    }

    // Expanded question bank with additional questions and difficulty levels
    private void initializeQuestionBank() {
        questionBank = new ArrayList<>();

        // Easy questions
        questionBank.add(new Question("What is smishing?",
                new String[]{"Scamming via SMS", "Phishing emails", "Online shopping fraud"}, 0, "Easy"));
        questionBank.add(new Question("What is phishing?",
                new String[]{"Sending fake emails to steal information", "Hacking websites", "Identity theft"}, 0, "Easy"));
        questionBank.add(new Question("How can you identify a smishing attempt?",
                new String[]{"Unexpected SMS with suspicious links", "Messages from known contacts", "Plain text messages"}, 0, "Easy"));
        questionBank.add(new Question("What should you do if you receive a phishing email?",
                new String[]{"Report it and avoid clicking any links", "Reply immediately", "Delete it without reporting"}, 0, "Easy"));
        questionBank.add(new Question("What does HTTPS indicate?",
                new String[]{"Secure website connection", "Fake website", "Malware link"}, 0, "Easy"));

        // Medium questions
        questionBank.add(new Question("Which of the following is a common sign of phishing?",
                new String[]{"Urgency in the message", "Formal tone", "Proper grammar"}, 0, "Medium"));
        questionBank.add(new Question("What is a common tactic used in smishing?",
                new String[]{"Asking for personal information", "Offering discounts", "Providing tracking information"}, 0, "Medium"));
        questionBank.add(new Question("How can you protect your device from smishing?",
                new String[]{"Avoid clicking unknown links", "Installing multiple apps", "Frequent software updates"}, 0, "Medium"));

        // Hard questions
        questionBank.add(new Question("What encryption protocol is commonly used for secure communications online?",
                new String[]{"TLS/SSL", "FTP", "SMTP"}, 0, "Hard"));
        questionBank.add(new Question("How does two-factor authentication enhance security?",
                new String[]{"Adds an extra layer of verification", "Speeds up login process", "Removes password requirement"}, 0, "Hard"));
    }

    private void displayQuestion() {
        optionsGroup.clearCheck();
        optionsGroup.removeAllViews();

        Question currentQuestion = questionBank.get(currentQuestionIndex);
        questionTextView.setText((currentQuestionIndex + 1) + ". " + currentQuestion.questionText);

        for (int i = 0; i < currentQuestion.options.length; i++) {
            RadioButton optionButton = new RadioButton(this);
            optionButton.setText(currentQuestion.options[i]);
            optionButton.setId(i);  // Set unique ID for each option
            optionsGroup.addView(optionButton);
        }
    }

    private void showResults() {
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("totalQuestions", questionBank.size());

        ArrayList<String> questions = new ArrayList<>();
        ArrayList<String[]> options = new ArrayList<>();
        ArrayList<Integer> correctAnswers = new ArrayList<>();

        for (Question question : questionBank) {
            questions.add(question.questionText);
            options.add(question.options);
            correctAnswers.add(question.correctOptionIndex);
        }

        intent.putStringArrayListExtra("questions", questions);
        intent.putExtra("options", options);
        intent.putIntegerArrayListExtra("userAnswers", new ArrayList<>(userAnswers));
        intent.putIntegerArrayListExtra("correctAnswers", new ArrayList<>(correctAnswers));

        startActivity(intent);
        finish();
    }
}
