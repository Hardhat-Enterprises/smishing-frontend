package com.example.smishingdetectionapp.ui.Register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.TermsAndConditionsActivity;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;

public class RegisterMain extends AppCompatActivity {

    private RegisterViewModel registerViewModel;

    private String email;
    private String BASE_URL = "http://10.0.2.2:5000/"; // Backend URL for local testing on emulator

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize ViewModel
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Set up UI components
        ImageButton backButton = findViewById(R.id.signup_back);
        TextView termsTextView = findViewById(R.id.terms_condition_checkbox);
        Button registerButton = findViewById(R.id.registerBtn);

        // Back Button
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Terms and Conditions click
        termsTextView.setOnClickListener(v -> {
            // Enable the Register button once the terms are clicked
            registerButton.setEnabled(true);
            Intent intent = new Intent(RegisterMain.this, TermsAndConditionsActivity.class);
            startActivityForResult(intent, 1001);
        });

        // Register Button - Handle user signup
        registerButton.setOnClickListener(v -> {
            EditText fullNameEditText = findViewById(R.id.full_name_input);
            EditText phoneNumberEditText = findViewById(R.id.pnInput);
            EditText emailEditText = findViewById(R.id.emailInput);
            EditText pinEditText = findViewById(R.id.pinInput);
            EditText passwordEditText = findViewById(R.id.pwInput);

            String fullName = fullNameEditText.getText().toString(); // Get the text entered by the user
            String phoneNumber = phoneNumberEditText.getText().toString(); // Get the text entered by the user
            email = emailEditText.getText().toString(); // Get the text entered by the user
            String password = passwordEditText.getText().toString(); // Get the text entered by the user

            if (validateInput(fullName, phoneNumber, email, password)) {
                registerViewModel.registerUser(fullName, phoneNumber, email, password);
            }
        });

        // Observe registration result from ViewModel
        registerViewModel.getSignupResponse().observe(this, signupResponse -> {
            if (signupResponse != null && signupResponse.isSuccess()) {
                Snackbar.make(findViewById(android.R.id.content), "Signup successful. Please verify your email.", Snackbar.LENGTH_LONG).show();
                // Navigate to verification screen (if needed)
                Intent intent = new Intent(RegisterMain.this, EmailVerify.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Signup failed. Please try again.", Snackbar.LENGTH_LONG).show();
            }
        });

        // Observe error message from ViewModel
        registerViewModel.getError().observe(this, error -> {
            if (error != null) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInput(String fullName, String phoneNumber, String email, String password) {
        if (TextUtils.isEmpty(fullName)) {
            Snackbar.make(findViewById(android.R.id.content), "Please enter your full name.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            Snackbar.make(findViewById(android.R.id.content), "Please enter a valid phone number.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!isValidEmailAddress(email)) {
            Snackbar.make(findViewById(android.R.id.content), "Please enter a valid email address.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        String confirmPassword = ((EditText) findViewById(R.id.pw2Input)).getText().toString(); // Fixed here
        if (password.length() < 8) {
            Snackbar.make(findViewById(android.R.id.content), "Password must be at least 8 characters long.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Snackbar.make(findViewById(android.R.id.content), "Passwords do not match.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Remove all non-digit characters (e.g., spaces, hyphens, parentheses)
        String cleanedPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");

        // Check if the cleaned phone number is valid (only digits)
        return cleanedPhoneNumber.matches("\\d+");
    }

    private boolean isValidEmailAddress(String email) {
        // Trim leading/trailing spaces
        String trimmedEmail = email.trim();

        // Use the Patterns.EMAIL_ADDRESS matcher to check for a valid email format
        return Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches();
    }
}

