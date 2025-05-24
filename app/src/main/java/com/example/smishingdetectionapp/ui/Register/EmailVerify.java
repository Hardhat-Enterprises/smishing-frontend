package com.example.smishingdetectionapp.ui.Register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.ui.login.LoginActivity;

public class EmailVerify extends AppCompatActivity {

    private VerifyViewModel verifyViewModel;
    private String email;
    private static final String TAG = "EmailVerify";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        verifyViewModel = new ViewModelProvider(this).get(VerifyViewModel.class);

        EditText verifyText = findViewById(R.id.verifytext);
        Button confirmBtn = findViewById(R.id.continueBtn); // Changed to use continueBtn from XML
//        ImageButton backBtn = findViewById(R.id.signup_back);

        email = getIntent().getStringExtra("email");
        Log.d(TAG, "Received email for OTP verification: " + email);

//        backBtn.setOnClickListener(v -> onBackPressed());

        confirmBtn.setOnClickListener(v -> {
            String otpCode = verifyText.getText().toString().trim();
            if (otpCode.length() == 6) {
                Log.d(TAG, "OTP code entered: " + otpCode);
                verifyViewModel.verifyOTP(email, otpCode);
            } else {
                Toast.makeText(this, "Enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
            }
        });

        verifyViewModel.getVerificationResponse().observe(this, response -> {
            if (response != null && response.isSuccess()) {
                Toast.makeText(this, "Email verified successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EmailVerify.this, LoginActivity.class));
                finish();
            } else if (response != null) {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

//package com.example.smishingdetectionapp.ui.Register;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.smishingdetectionapp.R;
//import com.example.smishingdetectionapp.ui.login.LoginActivity;
//
//public class EmailVerify extends AppCompatActivity {
//
//    private VerifyViewModel verifyViewModel;
//    private String email;
//    private static final String TAG = "EmailVerify";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_email_verify);
//
//        verifyViewModel = new ViewModelProvider(this).get(VerifyViewModel.class);
//
////        EditText verifyText = findViewById(R.id.verifytext);
////        Button confirmBtn = findViewById(R.id.confirmBtn);
//        ImageButton backBtn = findViewById(R.id.signup_back);
//
//        email = getIntent().getStringExtra("email"); // Get email from intent
//        Log.d(TAG, "Received email for OTP verification: " + email);
//
//        backBtn.setOnClickListener(v -> onBackPressed());
//
////        confirmBtn.setOnClickListener(v -> {
////            String otpCode = verifyText.getText().toString().trim();
////            if (otpCode.length() == 6) {
////                Log.d(TAG, "OTP code entered: " + otpCode);
////                verifyViewModel.verifyOTP(email, otpCode);
////            } else {
////                Toast.makeText(this, "Enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
////            }
////        });
//
//        verifyViewModel.getVerificationResponse().observe(this, response -> {
//            if (response != null && response.isSuccess()) {
//                Toast.makeText(this, "Email verified successfully!", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(EmailVerify.this, LoginActivity.class));
//                finish();
//            } else {
//                Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
