package com.example.smishingdetectionapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.ui.Submissionscreen;
import android.widget.ImageButton;

public class ContactUsActivity extends AppCompatActivity {

    private EditText email, firstName, lastName, company, phone, message;
    private CheckBox cb1, cb2, cb3, cb4;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // Input fields
        email = findViewById(R.id.email);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        company = findViewById(R.id.company);
        phone = findViewById(R.id.phone);
        message = findViewById(R.id.message);

        // Checkboxes
        cb1 = findViewById(R.id.checkbox_fake_sms);
        cb2 = findViewById(R.id.checkbox_feature_request);
        cb3 = findViewById(R.id.checkbox_feedback);
        cb4 = findViewById(R.id.checkbox_other);

        // Submit button
        submitButton = findViewById(R.id.submitContact);

        // Set up real-time validation
        setupValidation();

        // Submit button logic
        submitButton.setOnClickListener(v -> validateAndSubmit());
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());


        // Action bar setup
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Contact Us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupValidation() {
        email.addTextChangedListener(new GenericTextWatcher(email, true));
        firstName.addTextChangedListener(new GenericTextWatcher(firstName, false));
        lastName.addTextChangedListener(new GenericTextWatcher(lastName, false));
        company.addTextChangedListener(new GenericTextWatcher(company, false));
        message.addTextChangedListener(new GenericTextWatcher(message, false));
    }

    private class GenericTextWatcher implements TextWatcher {
        private final EditText field;
        private final boolean isEmail;

        public GenericTextWatcher(EditText field, boolean isEmail) {
            this.field = field;
            this.isEmail = isEmail;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isEmail) {
                if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    field.setError("Enter a valid email");
                } else {
                    field.setError(null);
                }
            } else {
                if (s.toString().trim().isEmpty()) {
                    field.setError("This field is required");
                } else {
                    field.setError(null);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    private void validateAndSubmit() {
        boolean isValid = true;

        // Get all field values
        String emailText = email.getText().toString().trim();
        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        String companyText = company.getText().toString().trim();
        String messageText = message.getText().toString().trim();

        // Email
        if (emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Valid email is required");
            isValid = false;
        }

        if (firstNameText.isEmpty()) {
            firstName.setError("First name is required");
            isValid = false;
        }

        if (lastNameText.isEmpty()) {
            lastName.setError("Last name is required");
            isValid = false;
        }

        if (companyText.isEmpty()) {
            company.setError("Company is required");
            isValid = false;
        }

        if (messageText.isEmpty()) {
            message.setError("Message is required");
            isValid = false;
        }

        if (!cb1.isChecked() && !cb2.isChecked() && !cb3.isChecked() && !cb4.isChecked()) {
            Toast.makeText(this, "Please select at least one request type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isValid) {
            Toast.makeText(this, "Your message has been sent!", Toast.LENGTH_LONG).show();

            // Clear the form
            email.setText("");
            firstName.setText("");
            lastName.setText("");
            company.setText("");
            phone.setText("");
            message.setText("");
            cb1.setChecked(false);
            cb2.setChecked(false);
            cb3.setChecked(false);
            cb4.setChecked(false);

            //  Launch the submission screen
            Intent intent = new Intent(ContactUsActivity.this, Submissionscreen.class);
            startActivity(intent);
        }
    }

        @Override
        public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
