package com.example.smishingdetectionapp;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import android.graphics.Color;
import android.app.AlertDialog;

import com.example.smishingdetectionapp.R;

//mohit start
public class RealVsSmishGame extends AppCompatActivity {
    private TextView message1, message2, feedbackLabel;  //mohit
    private TextView progressLabel; //leonora
    private ProgressBar progressBar; //leonora
    private Button nextButton; //mohit
    private Button restartButton, exitButton;  //leonora
    private List<Message[]> questionPairs = new ArrayList<>();  //mohit
    private int current = 0; //mohit
    private int score = 0;  //leonora
    private List<Message> realMessages = new ArrayList<>();  //leonora
    private List<Message> fakeMessages = new ArrayList<>();  //leonora
//mohit end

    //mohit start
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realvssmish); //leonora

        //mohit start
        message1 = findViewById(R.id.message1);
        message2 = findViewById(R.id.message2);
        feedbackLabel = findViewById(R.id.feedbackLabel);
        nextButton = findViewById(R.id.nextButton);
        //mohit end

        //leonora start
        progressLabel = findViewById(R.id.progressLabel);
        progressBar = findViewById(R.id.progressBar);
        restartButton = findViewById(R.id.restartButton);
        exitButton = findViewById(R.id.exitButton);
        //leonora end

        setupListeners();  //mohit
        loadRealMessages();  //leonora
        loadFakeMessages();  //leonora
        loadMessagePairs();  //mohit
        loadQuestion();  //mohit
    }

    //mohit start
    private void setupListeners() {
        message1.setOnClickListener(v -> checkAnswer(0));
        message2.setOnClickListener(v -> checkAnswer(1));

        nextButton.setOnClickListener(v -> nextQuestion());

        //leonora start
        restartButton.setOnClickListener(v -> {
            current = 0;
            score = 0;
            Collections.shuffle(questionPairs);
            loadQuestion();
            progressBar.setProgress(0);
            feedbackLabel.setText("Click the smishing message!");
            nextButton.setEnabled(false);
        });

        exitButton.setOnClickListener(v -> {
            // Show confirmation before exiting
            new AlertDialog.Builder(this)
                    .setTitle("Exit Game")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())  // Finish closes the activity
                    .setNegativeButton("No", null)
                    .show();
        });
        //leonora end
    }
    //mohit end

    //leonora start
    private void loadRealMessages() {
        realMessages.add(new Message("Your electricity bill is due 30/4/2025. Please ensure payment is submitted by 29/4/2025", false));
        realMessages.add(new Message("Your package is out for delivery. Estimated arrival is 29/3/2025", false));
        realMessages.add(new Message("REMINDER: Your appointment is scheduled for tomorrow at Berwick Medical Clinic", false));
        realMessages.add(new Message("Your Coles order has been processed. Estimated delivery time is 40 minutes.", false));
        realMessages.add(new Message("Telstra: Your prepaid recharge will expire in 3 days. Visit telstra.com/recharge package is out for delivery. Estimated arrival is 29/3/2025", false));
        realMessages.add(new Message("Your flight ticket to Brisbane has been sent to your email. Please click the embedded link to view.", false));
        realMessages.add(new Message("ATO: Your are due for a tax refund of $1,300.00. Check your myGov account.", false));
        realMessages.add(new Message("Your mobile service will be suspended due to unpaid bills. Contact customer support on 13 33 09 ", false));
        realMessages.add(new Message("Netflix: Unusual login detected. If this wasn't you, change your password at netflix.com/password", false));
    }

    private void loadFakeMessages() {
        fakeMessages.add(new Message("Your bank account has been compromised. Click the link to investigate http://bit.ly-21929749.com", true));
        fakeMessages.add(new Message("You've won a free iPhone. Click this link to claim http://free-iphone.com", true));
        fakeMessages.add(new Message("Your paypal account has been locked. To unlock your account, enter your credit card details at http://paypa1.com.au", true));
        fakeMessages.add(new Message("URGENT: You've been selected for a $500 Woolworths voucher. Claim at woolworths-now.win ", true));
        fakeMessages.add(new Message("Suspicious activity detected on your Amazon account. Log in here to secure it: amazon-check.account", true));
        fakeMessages.add(new Message("You missed jury duty. Avoid a fine by verifying your details at secure-court.govlink.com ", true));
        fakeMessages.add(new Message("ATO: You are going to be audited. View details and appeal now at ato-review.org", true));
        fakeMessages.add(new Message("Netflix: Payment failed. Update your card info at billing.netflix-secure.com", true));
        fakeMessages.add(new Message("Hey, it's mum. I'm stuck at the petrol station. Can you please send me $50 to BSB 09811 ACCT 171132987. Love you", true));
    }
    //leonora end

    //leonora start
    private void loadMessagePairs() {
        questionPairs.clear();

        int size = Math.min(realMessages.size(), fakeMessages.size());

        for (int i = 0; i < size; i++) {
            Message real = realMessages.get(i);
            Message fake = fakeMessages.get(i);
            if (i % 2 == 0) {
                questionPairs.add(new Message[]{real, fake});
            } else {
                questionPairs.add(new Message[]{fake, real});
            }
        }
        Collections.shuffle(questionPairs);
    }
    //leonora end

    //mohit start
    private void loadQuestion() {
        if (current >= questionPairs.size()) {
            showEndGameDialog();
            return;
        }

        Message[] pair = questionPairs.get(current);
        message1.setBackgroundColor(Color.WHITE);
        message2.setBackgroundColor(Color.WHITE);
        message1.setText(pair[0].getText());
        message2.setText(pair[1].getText());

        feedbackLabel.setText("Click the smishing message!");
        nextButton.setEnabled(false);
        progressLabel.setText("Question " + (current + 1) + " of " + questionPairs.size());  //leonora
    }

    private void checkAnswer(int selected) {
        Message[] pair = questionPairs.get(current);
        boolean correct = pair[selected].isSmishing();

        if (correct) {
            feedbackLabel.setText("Correct! That's the smishing message.");
            highlightSelection(selected, true);
            score++;
        } else {
            feedbackLabel.setText("Nope! That's a legit message.");
            highlightSelection(selected, false);
        }

        nextButton.setEnabled(true);
    }
    //mohit end

    //mohit start
    private void highlightSelection(int selected, boolean correct) {
        int color = correct ? Color.parseColor("#C8FFC8") : Color.parseColor("#FFC8C8");
        if (selected == 0) {
            message1.setBackgroundColor(color);
        } else {
            message2.setBackgroundColor(color);
        }
    }

    private void nextQuestion() {
        current++;
        progressBar.setProgress(current);
        loadQuestion();
    }
    //mohit end

    //leonora start
    private void showEndGameDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Your score: " + score + " / " + questionPairs.size() + "\nPlay again?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    current = 0;
                    score = 0;
                    loadMessagePairs();
                    loadQuestion();
                })
                .setNegativeButton("No", (dialog, which) -> finish())
                .show();
    }
}
//leonora end
