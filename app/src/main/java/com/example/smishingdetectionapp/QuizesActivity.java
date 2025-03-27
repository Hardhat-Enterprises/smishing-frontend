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


    class Question {
        String questionText;
        String[] options;
        int correctOptionIndex;

        Question(String questionText, String[] options, int correctOptionIndex) {
            this.questionText = questionText;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
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


        initializeQuestionBank();


        Collections.shuffle(questionBank);


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

    private void initializeQuestionBank() {
        questionBank = new ArrayList<>();
        questionBank.add(new Question("What is smishing?", new String[]{"Scamming via SMS", "Phishing emails", "Online shopping fraud"}, 0));
        questionBank.add(new Question("What is phishing?", new String[]{"Sending fake emails to steal information", "Hacking websites", "Identity theft"}, 0));
        questionBank.add(new Question("How can you identify a smishing attempt?", new String[]{"Unexpected SMS with suspicious links", "Messages from known contacts", "Plain text messages"}, 0));
        questionBank.add(new Question("What should you do if you receive a phishing email?", new String[]{"Report it and avoid clicking any links", "Reply immediately", "Delete it without reporting"}, 0));
        questionBank.add(new Question("What does HTTPS indicate?", new String[]{"Secure website connection", "Fake website", "Malware link"}, 0));
    }

    private void displayQuestion() {

        optionsGroup.clearCheck();
        optionsGroup.removeAllViews();


        Question currentQuestion = questionBank.get(currentQuestionIndex);
        questionTextView.setText((currentQuestionIndex + 1) + ". " + currentQuestion.questionText);


        for (int i = 0; i < currentQuestion.options.length; i++) {
            RadioButton optionButton = new RadioButton(this);
            optionButton.setText(currentQuestion.options[i]);
            optionButton.setId(i); // Set unique ID for each option
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
