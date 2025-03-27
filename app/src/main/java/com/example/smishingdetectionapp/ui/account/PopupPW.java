package com.example.smishingdetectionapp.ui.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.smishingdetectionapp.R;

public class PopupPW extends DialogFragment {

    private EditText editTextCurrentPassword, editTextNewPassword, editTextConfirmPassword;
    private Button changePW;
    private boolean isPasswordChange;

    // Create a new instance of PopupPW with an argument to indicate the mode
    public static PopupPW newInstance(boolean isPasswordChange) {
        PopupPW fragment = new PopupPW();
        Bundle args = new Bundle();
        args.putBoolean("isPasswordChange", isPasswordChange);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_password, container, false);

        // Initialize UI elements
        editTextCurrentPassword = v.findViewById(R.id.editTextTextCurrentPassword);
        editTextNewPassword = v.findViewById(R.id.editTextTextPassword);
        editTextConfirmPassword = v.findViewById(R.id.editTextTextPassword2);
        changePW = v.findViewById(R.id.change_pwBtn);

        // Get the arguments to determine the mode
        if (getArguments() != null) {
            isPasswordChange = getArguments().getBoolean("isPasswordChange", false);
        }

        // Hide current password field if it's not a password change
        if (!isPasswordChange) {
            editTextCurrentPassword.setVisibility(View.GONE);
        }

        // Add TextWatchers for real-time validation
        TextWatcher passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswords();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editTextNewPassword.addTextChangedListener(passwordTextWatcher);
        editTextConfirmPassword.addTextChangedListener(passwordTextWatcher);

        // Handle the button click
        changePW.setOnClickListener(v1 -> {
            if (validatePasswords()) {
                if (isPasswordChange) {
                    // Logic for changing password
                    Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Logic for creating or resetting password
                    Toast.makeText(getContext(), "Password created successfully", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please fix the errors", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    // Validate passwords and set errors if needed
    private boolean validatePasswords() {
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        boolean isValid = true;

        if (isPasswordChange && currentPassword.isEmpty()) {
            editTextCurrentPassword.setError("Current password is required");
            isValid = false;
        }

        if (newPassword.isEmpty()) {
            editTextNewPassword.setError("New password is required");
            isValid = false;
        } else if (!isValidPassword(newPassword)) {
            editTextNewPassword.setError("Password must be at least 8 characters long and include a mix of letters, numbers, and special characters.");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.setError("Please confirm your new password");
            isValid = false;
        } else if (!newPassword.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        changePW.setEnabled(isValid);
        return isValid;
    }

    // Check if the password meets the required criteria
    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        if (!password.matches(".*[!@#$%^&*+=?-].*")) {
            return false;
        }

        return true;
    }
}
