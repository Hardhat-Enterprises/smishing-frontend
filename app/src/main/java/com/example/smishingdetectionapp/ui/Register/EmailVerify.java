package com.example.smishingdetectionapp.ui.Register;



import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.DataBase.SignupResponse;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.databinding.ActivityEmailVerifyBinding;
import com.example.smishingdetectionapp.databinding.ActivitySignupBinding;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmailVerify extends AppCompatActivity {

    private String email;
    private String fullName;
    private String phoneNumber;
    private String password;
    private String verificationCode;
    private EditText verificationCodeInput;
    private Button verifyButton;

    private ActivityEmailVerifyBinding binding;

    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private String BASE_URL = BuildConfig.SERVERIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEmailVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitinterface = retrofit.create(Retrofitinterface.class);

        ImageButton imageButton = findViewById(R.id.signup_back);
        imageButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Get the data passed from the RegisterMain activity
        Intent intent = getIntent();
        fullName = intent.getStringExtra("fullName");
        phoneNumber = intent.getStringExtra("phoneNumber");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        verificationCode = intent.getStringExtra("code");



        verificationCodeInput = findViewById(R.id.verifytext);
        verifyButton = findViewById(R.id.confirmBtn);

        /*
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = verificationCodeInput.getText().toString();



                // Check if the entered code matches the one sent via email
                if (enteredCode.equals(verificationCode)) {
                    Snackbar.make(binding.getRoot(), "Email verified successfully.", Snackbar.LENGTH_LONG).show();


                    // Proceed with the signup process after successful verification
                    completeSignup();
                } else {
                    Snackbar.make(binding.getRoot(), "Invalid verification code. Please try again.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
         */

        // Bypass the email verification for testing purposes
        verifyButton.setOnClickListener(v -> {
            // Show a message indicating the verification is skipped
            Snackbar.make(binding.getRoot(), "Email verified successfully (bypassed for testing).", Snackbar.LENGTH_LONG).show();
            completeSignup();  // Proceed with signup
        });
    }

    /*
    private void completeSignup() {
        HashMap<String, String> map = new HashMap<>();
        map.put("FullName", fullName);
        map.put("PhoneNumber", phoneNumber);
        map.put("Email", email);
        map.put("Password", password);

        Call<SignupResponse> call = retrofitinterface.executeSignup(map);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(binding.getRoot(), "Registration successful.", Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(EmailVerify.this, MainActivity.class);
                    startActivity(intent);

                } else if (response.code() == 409) {
                    Snackbar.make(binding.getRoot(), "Email already exists.", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(binding.getRoot(), "Signup failed. Try again.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                Snackbar.make(binding.getRoot(), t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

     */
    // Bypassing verification for testing purposes
    private void completeSignup() {
        // Directly simulate a successful signup
        Snackbar.make(binding.getRoot(), "Registration successful (bypassed).", Snackbar.LENGTH_LONG).show();

        // After successful registration, navigate to MainActivity
        Intent intent = new Intent(EmailVerify.this, MainActivity.class);
        startActivity(intent);
    }

}
