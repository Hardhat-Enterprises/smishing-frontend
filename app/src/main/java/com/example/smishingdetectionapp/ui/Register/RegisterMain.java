package com.example.smishingdetectionapp.ui.Register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.DataBase.SignupResponse;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.TermsAndConditionsActivity;
import com.example.smishingdetectionapp.databinding.ActivitySignupBinding;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterMain extends AppCompatActivity {

    private static final int TERMS_REQUEST_CODE = 1001;  // Unique request code for terms acceptance
    private ActivitySignupBinding binding;
    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private String BASE_URL = BuildConfig.SERVERIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up view binding
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitinterface = retrofit.create(Retrofitinterface.class);

        // Set up back button
        ImageButton backButton = findViewById(R.id.signup_back);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Link Terms and Conditions
        TextView termsTextView = findViewById(R.id.terms_conditions);
        termsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterMain.this, TermsAndConditionsActivity.class);
            startActivityForResult(intent, TERMS_REQUEST_CODE);
        });

        // Set up register button
        Button registerButton = findViewById(R.id.registerBtn);
        registerButton.setEnabled(false);

        // Set up registration logic
        registerButton.setOnClickListener(v -> {
            String fullName = binding.fullNameInput.getText().toString();
            String phoneNumber = binding.pnInput.getText().toString();
            String email = binding.emailInput.getText().toString();
            String password = binding.pwInput.getText().toString();

            if (validateInput(fullName, phoneNumber, email, password)) {
                validateAndCheckEmail(fullName, phoneNumber, email, password);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if we are handling the result from the Terms and Conditions activity
        if (requestCode == TERMS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Terms accepted, enable the register button
                Button registerButton = findViewById(R.id.registerBtn);
                registerButton.setEnabled(true);
            }
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generate a random 6-digit code
        return String.valueOf(code);
    }

    private void sendVerificationEmail(String email, String verificationCode) {
        String subject = "Your Verification Code";
        String message = "Your verification code is: " + verificationCode;

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email, subject, message);
        javaMailAPI.execute();
    }

    private boolean validateInput(String fullName, String phoneNumber, String email, String password) {
        if (TextUtils.isEmpty(fullName)) {
            Snackbar.make(binding.getRoot(), "Please enter your full name.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            Snackbar.make(binding.getRoot(), "Please enter a valid phone number.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!isValidEmailAddress(email)) {
            Snackbar.make(binding.getRoot(), "Please enter a valid email address.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        String confirmPassword = binding.pw2Input.getText().toString();
        if (password.length() < 8) {
            Snackbar.make(binding.getRoot(), "Password must be at least 8 characters long.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Snackbar.make(binding.getRoot(), "Passwords do not match.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            Snackbar.make(binding.getRoot(), "Password must include a number.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            Snackbar.make(binding.getRoot(), "Password must include an uppercase letter.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            Snackbar.make(binding.getRoot(), "Password must include a lowercase letter.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!password.matches(".*[!@#$%^&*+=?-].*")) {
            Snackbar.make(binding.getRoot(), "Password must include a special character.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /*
    private void validateAndCheckEmail(final String fullName, final String phoneNumber, final String email, final String password) {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);

        Call<SignupResponse> call = retrofitinterface.checkEmail(map);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful()) {
                    String verificationCode = generateVerificationCode();
                    sendVerificationEmail(email, verificationCode);

                    Intent intent = new Intent(RegisterMain.this, EmailVerify.class);
                    intent.putExtra("fullName", fullName);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("code", verificationCode);
                    startActivity(intent);
                } else if (response.code() == 409) {
                    Snackbar.make(binding.getRoot(), "Email already exists.", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(binding.getRoot(), "Error checking email. Please try again.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                Snackbar.make(binding.getRoot(), "Network error. Please try again.", Snackbar.LENGTH_LONG).show();
            }
        });
    }
     */
    // Bypassing verification for testing purposes
    private void validateAndCheckEmail(final String fullName, final String phoneNumber, final String email, final String password) {
        // Instead of calling the server, simulate a successful email check
        String verificationCode = generateVerificationCode();

        // Simulate sending the verification code via email
        sendVerificationEmail(email, verificationCode);

        // Skip the network call and directly navigate to the email verification screen
        Intent intent = new Intent(RegisterMain.this, EmailVerify.class);
        intent.putExtra("fullName", fullName);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("code", verificationCode);
        startActivity(intent);
    }


    private boolean isValidEmailAddress(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();

            String emailPattern = "^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
            return Pattern.matches(emailPattern, email) && !email.contains("..") && !email.startsWith(".") && !email.endsWith(".");
        } catch (AddressException ex) {
            return false;
        }
    }
}