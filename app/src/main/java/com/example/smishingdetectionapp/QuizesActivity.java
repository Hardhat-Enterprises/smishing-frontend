package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizesActivity extends AppCompatActivity {

    // Updated inner Question class with a hint field added.
    class Question {
        String questionText;
        String[] options;
        int correctOptionIndex;
        String hint;  // NEW FIELD

        Question(String questionText, String[] options, int correctOptionIndex, String hint) {
            this.questionText = questionText;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
            this.hint = hint;
        }
    }

    private List<Question> questionBank;
    private List<Integer> userAnswers;
    private int currentQuestionIndex = 0;
    private int score = 0;

    // Timer-related fields
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private static final long QUESTION_TIME = 15000; // 15 seconds per question
    private long questionStartTime;
    private ArrayList<Integer> timeSpentPerQuestion;

    private TextView questionTextView;
    private RadioGroup optionsGroup;
    private Button nextButton;
    private Button hintButton;  // NEW: Button for showing a hint

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Find views from the layout.
        questionTextView = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);
        timerTextView = findViewById(R.id.timerText);  // Timer TextView in layout
        hintButton = findViewById(R.id.hintButton);      // NEW: Hint Button in layout

        initializeQuestionBank();
        timeSpentPerQuestion = new ArrayList<>();

        // Shuffle the question bank and select 5 random questions
        Collections.shuffle(questionBank);
        questionBank = questionBank.subList(0, 5);

        // Initialize user answers (default value -1 means no answer)
        userAnswers = new ArrayList<>(Collections.nCopies(questionBank.size(), -1));

        displayQuestion();

        // Set listener for the Hint Button.
        hintButton.setOnClickListener(v -> {
            // Show a Toast with the hint for the current question.
            Toast.makeText(this, "Hint: " + questionBank.get(currentQuestionIndex).hint, Toast.LENGTH_SHORT).show();
        });

        // Next button click listener.
        nextButton.setOnClickListener(v -> {
            int selectedOptionId = optionsGroup.getCheckedRadioButtonId();
            if (selectedOptionId == -1) {
                Toast.makeText(this, "Please select an answer before proceeding!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Cancel the timer if an answer was selected in time.
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            // Record time spent on this question (in seconds)
            int timeTaken = (int) ((System.currentTimeMillis() - questionStartTime) / 1000);
            timeSpentPerQuestion.add(timeTaken);

            int selectedOptionIndex = optionsGroup.indexOfChild(findViewById(selectedOptionId));
            userAnswers.set(currentQuestionIndex, selectedOptionIndex);

            if (selectedOptionIndex == questionBank.get(currentQuestionIndex).correctOptionIndex) {
                score++;
            }
            moveToNextQuestion();
        });

        ImageButton report_back = findViewById(R.id.quiz_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, EducationActivity.class));
            finish();
        });
    }

    private void displayQuestion() {
        // Clear previous selections and remove old options.
        optionsGroup.clearCheck();
        optionsGroup.removeAllViews();

        Question currentQuestion = questionBank.get(currentQuestionIndex);
        questionTextView.setText((currentQuestionIndex + 1) + ". " + currentQuestion.questionText);

        // Dynamically create radio buttons for each option.
        for (int i = 0; i < currentQuestion.options.length; i++) {
            RadioButton optionButton = new RadioButton(this);
            optionButton.setText(currentQuestion.options[i]);
            optionButton.setId(i); // Unique ID for each option
            optionsGroup.addView(optionButton);
        }

        // Start the countdown timer for this question.
        questionStartTime = System.currentTimeMillis();
        startCountDownTimer();
    }

    // Starts the 15-second countdown for each question.
    private void startCountDownTimer() {
        // Cancel any existing timer.
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(QUESTION_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time: " + millisUntilFinished / 1000 + " sec");
            }

            @Override
            public void onFinish() {
                timerTextView.setText("Time's up!");
                // Record full time (15 sec) if timer runs out.
                timeSpentPerQuestion.add((int) (QUESTION_TIME / 1000));
                // Auto-move to next question; leave answer as -1 (unanswered).
                moveToNextQuestion();
            }
        }.start();
    }

    // Moves to the next question or shows results if finished.
    private void moveToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionBank.size()) {
            displayQuestion();
        } else {
            showResults();
        }
    }

    private void initializeQuestionBank() {
        questionBank = new ArrayList<>();
        // Provide hints for each question as the fourth parameter.
        questionBank.add(new Question(
                "What is smishing?",
                new String[]{"Scamming via SMS", "Phishing emails", "Online shopping fraud"},
                0,
                "Consider how SMS messages can be used for scams."
        ));
        questionBank.add(new Question(
                "What is phishing?",
                new String[]{"Sending fake emails to steal information", "Hacking websites", "Identity theft"},
                0,
                "Think about the method that targets your email inbox."
        ));
        questionBank.add(new Question(
                "How can you identify a smishing attempt?",
                new String[]{"Unexpected SMS with suspicious links", "Messages from known contacts", "Plain text messages"},
                0,
                "Look for unusual requests or links in text messages."
        ));
        questionBank.add(new Question(
                "What should you do if you receive a phishing email?",
                new String[]{"Report it and avoid clicking any links", "Reply immediately", "Delete it without reporting"},
                0,
                "Your first action should be to report and avoid engagement."
        ));
        questionBank.add(new Question(
                "What does HTTPS indicate?",
                new String[]{"Secure website connection", "Fake website", "Malware link"},
                0,
                "Notice the 'S' at the end meaning 'secure'."
        ));
        // New questions.
        questionBank.add(new Question(
                "Which action helps prevent smishing?",
                new String[]{"Avoid clicking unknown SMS links", "Always reply to unknown SMS", "Give personal info in messages"},
                0,
                "Being cautious with unknown links is key."
        ));
        questionBank.add(new Question(
                "What’s a sign of a smishing message?",
                new String[]{"Urgent tone with a link", "From your contact list", "Proper grammar"},
                0,
                "Urgency and suspicious links are often a red flag."
        ));
        questionBank.add(new Question(
                "Smishing is mostly delivered via?",
                new String[]{"Text Messages", "Emails", "Phone Calls"},
                0,
                "Focus on the medium that sends SMS messages."
        ));
        questionBank.add(new Question(
                "Who is most likely to fall for smishing?",
                new String[]{"Unaware users", "Cybersecurity experts", "Bank managers"},
                0,
                "Less cautious or unaware users are often targeted."
        ));
        questionBank.add(new Question(
                "A legitimate bank message will usually:",
                new String[]{"Not ask for passwords", "Ask for PIN", "Ask for OTP"},
                0,
                "Banks rarely ask for your password in messages."
        ));
        questionBank.add(new Question(
                "Which one is safe to click?",
                new String[]{"Link from verified app notification", "Random SMS link", "Unknown sender’s URL"},
                0,
                "Links from verified sources are typically safe."
        ));
        questionBank.add(new Question(
                "Smishing messages aim to:",
                new String[]{"Steal credentials", "Help security", "Inform public"},
                0,
                "The motive is often to steal sensitive data."
        ));
        questionBank.add(new Question(
                "When unsure about a message, you should:",
                new String[]{"Contact the institution directly", "Click the link to check", "Reply asking who it is"},
                0,
                "Verify using known, trusted contact information."
        ));
        questionBank.add(new Question(
                "What does a shortened URL in SMS often indicate?",
                new String[]{"Possible fraud link", "Verified site", "Encrypted page"},
                0,
                "Shortened URLs can hide malicious destinations."
        ));
        questionBank.add(new Question(
                "What should you do if you clicked a smishing link?",
                new String[]{"Disconnect and scan device", "Ignore it", "Reply to sender"},
                0,
                "Your first step should be to secure your device."
        ));
    }

    private void showResults() {
        // Cancel any running timer.
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
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
        intent.putIntegerArrayListExtra("timeSpent", timeSpentPerQuestion); // Pass per-question time

        startActivity(intent);
        finish();
    }
}
