package com.example.smishingdetectionapp.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.DBresult;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.databinding.ActivityLoginBinding;
import com.example.smishingdetectionapp.ui.Register.RegisterMain;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private String BASE_URL = BuildConfig.SERVERIP;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private boolean isPinLogin = false;  // Flag for PIN login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitinterface = retrofit.create(Retrofitinterface.class);

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        // ViewModel setup - Updated to use AndroidViewModel
        loginViewModel = new ViewModelProvider(this,
                new LoginViewModelFactory(getApplication()))
                .get(LoginViewModel.class);

        // View bindings
        final EditText usernameEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.loginButton;
        final ProgressBar loadingProgressBar = binding.progressbar;
        final SignInButton googleBtn = binding.googleBtn;
        final Button registerButton = binding.registerButton;
        final ImageButton togglePasswordVisibility = binding.togglePasswordVisibility;
        final Button togglePinLogin = binding.togglePinLogin;

        // Toggle functionality for PIN and Password login
        togglePinLogin.setOnClickListener(v -> {
            if (isPinLogin) {
                // Switch to password login
                passwordEditText.setHint("Password");
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                loginButton.setText("Login");
                togglePinLogin.setText("Login with PIN");
                isPinLogin = false;
            } else {
                // Switch to PIN login
                passwordEditText.setHint("Enter 6-digit PIN");
                passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                loginButton.setText("Login with PIN");
                togglePinLogin.setText("Login with Password");
                isPinLogin = true;
            }
        });

        // Handle login button click
        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            String input = passwordEditText.getText().toString();
            if (isPinLogin) {
                // Handle PIN login
                if (input.length() != 6) {
                    passwordEditText.setError("PIN must be 6 digits");
                    loadingProgressBar.setVisibility(View.GONE);
                    return;
                }
                loginWithPin(input);
            } else {
                // Handle password login
                String email = usernameEditText.getText().toString();
                if (email.isEmpty() || input.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email and Password must not be empty", Toast.LENGTH_SHORT).show();
                    loadingProgressBar.setVisibility(View.GONE);
                    return;
                }
                // Use the new login method with repository pattern
                loginViewModel.login(email, input);
            }
        });

        // Handle register button click
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterMain.class));
            finish();
        });

        // Handle Google Sign-In setup
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        // Sign out of Google account to allow fresh authentication
        gsc.signOut().addOnCompleteListener(task -> {
            Toast.makeText(this, "Signed out. Ready for fresh authentication.", Toast.LENGTH_SHORT).show();
        });

        // Handle Google Sign-In button click
        googleBtn.setOnClickListener(v -> {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                signOutGoogle(() -> signInGoogle());
            } else {
                signInGoogle();
            }
        });

        // Observe LoginFormState
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) return;
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        // Observe LoginResponse - NEW
        loginViewModel.getLoginResponse().observe(this, response -> {
            loadingProgressBar.setVisibility(View.GONE);
            if (response != null && response.isSuccess()) {
                // Handle successful login
                Toast.makeText(LoginActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                // Save token if provided
                // if (response.getToken() != null && !response.getToken().isEmpty()) {
                //     saveAuthToken(response.getToken());
                // }
                navigateToMainActivity();
            }
        });

        // Observe error messages - NEW
        loginViewModel.getError().observe(this, error -> {
            loadingProgressBar.setVisibility(View.GONE);
            if (error != null) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });

        // Observe legacy LoginResult (for compatibility)
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        // Password visibility toggle
        togglePasswordVisibility.setOnClickListener(v -> {
            boolean isPasswordVisible = passwordEditText.getTransformationMethod() == null;
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.ic_passwords_visibility);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                togglePasswordVisibility.setImageResource(R.drawable.ic_passwords_visibility);
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });
    }

    // Google Sign-In
    void signInGoogle() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    // Google Sign-Out
    void signOutGoogle(Runnable onSignOutComplete) {
        gsc.signOut().addOnCompleteListener(task -> {
            Toast.makeText(this, "Signed out of Google account.", Toast.LENGTH_SHORT).show();
            onSignOutComplete.run();
        });
    }

    // Handle the result of the Google Sign-In
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToMainActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginWithPin(String pin) {
        // For testing purposes, simulate a successful PIN login
        Toast.makeText(LoginActivity.this, "PIN verified successfully (bypassed for testing)", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    // The old method is no longer used directly but kept for compatibility
    private void loginWithPassword(String email, String password) {
        // Now redirects to the ViewModel login method
        loginViewModel.login(email, password);
    }

    // Legacy method kept for compatibility
    private void handleLoginDialog() {
        final EditText usernameEditText = binding.email;
        final EditText passwordEditText = binding.password;

        HashMap<String, String> map = new HashMap<>();
        map.put("email", usernameEditText.getText().toString());
        map.put("password", passwordEditText.getText().toString());

        Call<DBresult> call = retrofitinterface.executeLogin(map);
        call.enqueue(new Callback<DBresult>() {
            @Override
            public void onResponse(Call<DBresult> call, Response<DBresult> response) {
                if (response.code() == 200) {
                    navigateToMainActivity();
                } else if (response.code() == 404) {
                    Toast.makeText(LoginActivity.this, "Wrong Credentials", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DBresult> call, Throwable throwable) {
                Toast.makeText(LoginActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                navigateToMainActivity();
            }
        });
    }

    private boolean isUserLoggedIn() {
        // Placeholder for checking login state
        return false;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

}