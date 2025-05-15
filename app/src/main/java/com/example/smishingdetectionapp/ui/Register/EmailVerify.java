package com.example.smishingdetectionapp.ui.Register;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private int otpLength = 4; // make this dynamic if needed
    private EditText[] otpFields;
    private String verificationCode;
    private String email, fullName, phoneNumber, password;

    private TextView resendText;
    private Button verifyButton;

    private ActivityEmailVerifyBinding binding;

    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private String BASE_URL = BuildConfig.SERVERIP;
    private CountDownTimer timer;

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

        resendText = findViewById(R.id.resendText);
        verifyButton = findViewById(R.id.continueBtn);

        LinearLayout otpContainer = findViewById(R.id.otpContainer);
        otpFields = new EditText[otpLength];

        for (int i = 0; i < otpLength; i++) {
            EditText editText = new EditText(this);
            editText.setLayoutParams(new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT));
            editText.setGravity(Gravity.CENTER);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            editText.setTextSize(24);
            editText.setId(View.generateViewId());

            final int index = i;
            editText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < otpLength - 1) {
                        otpFields[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otpFields[index - 1].requestFocus();
                    }
                }
            });

            otpFields[i] = editText;
            otpContainer.addView(editText);
        }

        otpFields[0].requestFocus();
        startTimer(20); // 20 seconds timer

        // Back to login
        findViewById(R.id.signup_back).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        verifyButton.setOnClickListener(v -> {
            String enteredOTP = getEnteredOTP();
            if (enteredOTP.length() != otpLength) {
                Snackbar.make(findViewById(android.R.id.content), "Please enter full code.", Snackbar.LENGTH_LONG).show();
                return;
            }

            // Uncomment below for real verification
            /*
            if (!enteredOTP.equals(verificationCode)) {
                Snackbar.make(findViewById(android.R.id.content), "Invalid verification code.", Snackbar.LENGTH_LONG).show();
                return;
            }
            */

            Snackbar.make(findViewById(android.R.id.content), "Email verified successfully.", Snackbar.LENGTH_LONG).show();
            completeSignup();
        });
    }

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

    private void startTimer(int seconds) {
        resendText.setTextColor(Color.GRAY);
        timer = new CountDownTimer(seconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                resendText.setText("Resend code in 00:" + String.format("%02d", millisUntilFinished / 1000));
            }

            public void onFinish() {
                resendText.setText("Resend Code");
                resendText.setTextColor(Color.BLUE);
                resendText.setOnClickListener(v -> {
                    Snackbar.make(findViewById(android.R.id.content), "Code re-sent.", Snackbar.LENGTH_SHORT).show();
                    resendText.setOnClickListener(null);
                    startTimer(20);
                });
            }
        }.start();
    }

    private String getEnteredOTP() {
        StringBuilder otp = new StringBuilder();
        for (EditText field : otpFields) {
            otp.append(field.getText().toString());
        }
        return otp.toString();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        super.onDestroy();
    }
}
