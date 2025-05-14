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
import com.example.smishingdetectionapp.detections.DatabaseAccess;

public class PopupPIN extends DialogFragment {

    private EditText editTextNewPIN, editTextConfirmPIN;
    private Button changePIN;

    private DatabaseAccess databaseAccess;

    // Create a new instance of PopupPIN
    public static PopupPIN newInstance() {
        return new PopupPIN();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_pin, container, false);

        // Initialize UI elements
        editTextNewPIN = v.findViewById(R.id.editTextNewPIN);
        editTextConfirmPIN = v.findViewById(R.id.editTextConfirmPIN);
        changePIN = v.findViewById(R.id.change_pinBtn);

        // Initialize DatabaseAccess
        databaseAccess = DatabaseAccess.getInstance(getContext());
        databaseAccess.open();

        // Add TextWatchers for real-time validation
        TextWatcher pinTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePINs();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editTextNewPIN.addTextChangedListener(pinTextWatcher);
        editTextConfirmPIN.addTextChangedListener(pinTextWatcher);

        // Handle the button click
        changePIN.setOnClickListener(v1 -> {
            if (validatePINs()) {
                String newPIN = editTextNewPIN.getText().toString().trim();

                // Logic for creating or resetting PIN
                boolean isCreated = databaseAccess.createPIN(newPIN);
                if (isCreated) {
                    Toast.makeText(getContext(), "PIN created successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Error creating PIN", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please fix the errors", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    // Validate PINs and set errors if needed
    private boolean validatePINs() {
        String newPIN = editTextNewPIN.getText().toString().trim();
        String confirmPIN = editTextConfirmPIN.getText().toString().trim();

        boolean isValid = true;

        if (newPIN.isEmpty()) {
            editTextNewPIN.setError("New PIN is required");
            isValid = false;
        } else if (!isValidPIN(newPIN)) {
            editTextNewPIN.setError("PIN must be exactly 6 digits long.");
            isValid = false;
        }

        if (confirmPIN.isEmpty()) {
            editTextConfirmPIN.setError("Please confirm your new PIN");
            isValid = false;
        } else if (!newPIN.equals(confirmPIN)) {
            editTextConfirmPIN.setError("PINs do not match");
            isValid = false;
        }

        changePIN.setEnabled(isValid);
        return isValid;
    }

    // Check if the PIN meets the required criteria
    private boolean isValidPIN(String pin) {
        return pin.length() == 6 && pin.matches("\\d{6}");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseAccess != null) {
            databaseAccess.close();
        }
    }
}
