package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pwned Email Detector Activity added by Hash
 */
public class PwnedEmailCheckerActivity extends AppCompatActivity {

    private static final ArrayList<String> pwnedEmailList = new ArrayList<String>();
    // hard coded emails added for pwned email list.
    static {
        pwnedEmailList.add("hacker_dark@web.com");
        pwnedEmailList.add("serialcraker@abc.com");
        pwnedEmailList.add("devilforceone@hack.com");
    }

    private static final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pwned_email_checker);
        detechPwnedEmailButtonClickListner();
    }

    private void detechPwnedEmailButtonClickListner() {
        button = findViewById(R.id.EmailCheckBtn);
        // Set OnClickListener for the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = findViewById(R.id.emailInput);
                String emailTxt = email.getText().toString();
                // Handle password login
                if (emailTxt.isEmpty()) {
                    Toast.makeText(PwnedEmailCheckerActivity.this, "Email must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isValidEmail(emailTxt)) {
                    Toast.makeText(PwnedEmailCheckerActivity.this, "Email is not valid", Toast.LENGTH_SHORT).show();
                } else {
                    if (pwnedEmailList.contains(emailTxt)) {
                        Toast.makeText(PwnedEmailCheckerActivity.this, "Alert! This Email" + emailTxt + " is already Pwned", Toast.LENGTH_LONG).show();
                    } else {
                        String text = "This Email Address " + emailTxt + " is not Pwned";
                        Toast.makeText(PwnedEmailCheckerActivity.this, text,Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}