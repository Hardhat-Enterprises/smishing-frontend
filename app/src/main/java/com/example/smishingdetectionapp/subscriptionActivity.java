package com.example.smishingdetectionapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Added for NewsLetter subscription by Hash
 */
public class subscriptionActivity extends AppCompatActivity {
    private static final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    public Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subscription);
        setupButtonClickListner();
    }

    private void setupButtonClickListner() {
        button = findViewById(R.id.subscribeBtn);
        // Set OnClickListener for the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstName = findViewById(R.id.first_name_input);
                EditText lastName = findViewById(R.id.last_name_input);
                EditText email = findViewById(R.id.emailInput);
                CheckBox checkBox = findViewById(R.id.checkBox_terms_conditions);
                String firstNameTxt = firstName.getText().toString();
                String lastNameTxt =  lastName.getText().toString();
                String emailTxt = email.getText().toString();
                // Handle password login
                if (emailTxt.isEmpty() || firstNameTxt.isEmpty() || lastNameTxt.isEmpty()) {
                        Toast.makeText(subscriptionActivity.this, "First Name, Last Name and Email must not be empty", Toast.LENGTH_SHORT).show();
                        return;
                } else if (!isValidEmail(emailTxt)) {
                    Toast.makeText(subscriptionActivity.this, "Email is not valid", Toast.LENGTH_SHORT).show();
                } else if (!checkBox.isChecked()) {
                    Toast.makeText(subscriptionActivity.this, "Please accept terms & conditions", Toast.LENGTH_SHORT).show();
                } else {
                    String text = "User " + firstNameTxt + " "+lastNameTxt + " is subscribed to newsletter";
                    Toast.makeText(subscriptionActivity.this, text,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}