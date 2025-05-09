package com.example.smishingdetectionapp;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smishingdetectionapp.detections.YourReportsActivity;
public class ReportingActivity extends SharedActivity {


    private static final int IMAGE_PICK_REQUEST = 100;
    private TextView tvSelectedImage;
    private ConstraintLayout step1Container, step2Container;
    private ProgressBar progressBarSteps;
    private Button btnNext, btnBackStep1, btnSelectImage, btnSubmitReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reporting);


        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Toolbar buttons
        ImageButton reportBack = findViewById(R.id.report_back);
        reportBack.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });


        ImageButton menuButton = findViewById(R.id.report_menu);
        menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(ReportingActivity.this, menuButton);
            popup.getMenuInflater().inflate(R.menu.report_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.saved_reports) {
                    startActivity(new Intent(ReportingActivity.this, YourReportsActivity.class));
                    return true;
                }
                return false;
            });
            popup.show();
        });


        // Initialize progress bar and containers
        progressBarSteps = findViewById(R.id.progressBarSteps);
        step1Container = findViewById(R.id.step1Container);
        step2Container = findViewById(R.id.step2Container);


        // Initialize Step 1 fields
        EditText etName       = findViewById(R.id.etName);
        EditText etEmail      = findViewById(R.id.etEmail);
        EditText etMobile     = findViewById(R.id.etMobile);
        // ... other Step 1 fields as needed


        // Initialize Step 2 fields
        EditText etDescription  = findViewById(R.id.etDescription);
        // ... other Step 2 fields as needed


        // Initialize Buttons
        btnNext         = findViewById(R.id.btnNext);
        btnBackStep1    = findViewById(R.id.btnBackStep1);
        btnSelectImage  = findViewById(R.id.btnSelectImage);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
        tvSelectedImage = findViewById(R.id.tvSelectedImage);


        // Default to Step 1
        showStep1();


        // TextWatcher for Step 1: require Name, Email, and Mobile
        TextWatcher step1Watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean canEnable = !etName.getText().toString().isEmpty() &&
                        !etEmail.getText().toString().isEmpty() &&
                        !etMobile.getText().toString().isEmpty();
                btnNext.setEnabled(canEnable);
            }
            @Override public void afterTextChanged(Editable s) { }
        };
        etName.addTextChangedListener(step1Watcher);
        etEmail.addTextChangedListener(step1Watcher);
        etMobile.addTextChangedListener(step1Watcher);


        // TextWatcher for Step 2: require Description
        TextWatcher step2Watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean canEnable = !etDescription.getText().toString().isEmpty();
                btnSubmitReport.setEnabled(canEnable);
            }
            @Override public void afterTextChanged(Editable s) { }
        };
        etDescription.addTextChangedListener(step2Watcher);


        // Next Button: Validate and move to Step 2
        btnNext.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            if (!isValidEmail(email)) {
                Toast.makeText(getApplicationContext(), "Invalid email!", Toast.LENGTH_SHORT).show();
                return;
            }
            showStep2();
        });


        // Back Button: Return to Step 1
        btnBackStep1.setOnClickListener(v -> showStep1());


        // Select Image Button: Launch image picker
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_PICK_REQUEST);
        });


        // Submit Button: Validate and submit report
        btnSubmitReport.setOnClickListener(v -> {
            String description = etDescription.getText().toString().trim();
            if (description.length() < 5) {
                Toast.makeText(getApplicationContext(), "Description too short!", Toast.LENGTH_SHORT).show();
                return;
            }
            // For DB insertion, using mobile number and description
            String mobileNumber = etMobile.getText().toString().trim();
            boolean isInserted = DatabaseAccess.sendReport(mobileNumber, description);


            if (isInserted) {
                AlertDialog dialog = new AlertDialog.Builder(ReportingActivity.this)
                        .setIcon(R.drawable.ic_tick)  // Tick icon
                        .setTitle("Success")
                        .setMessage("Report submitted successfully!")
                        .setPositiveButton("OK", (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                            // Reset fields
                            etName.setText("");
                            etEmail.setText("");
                            etMobile.setText("");
                            etDescription.setText("");
                            tvSelectedImage.setText("");
                            btnSelectImage.setText("Select Image (optional)");
                            showStep1();
                        })
                        .create();


                dialog.show();


                // Customize the positive (OK) button
                Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setBackgroundResource(R.drawable.custom_button_background);  // Custom drawable for button background
                okButton.setTextColor(getResources().getColor(android.R.color.black));
                // Optionally, set additional properties like text size, padding, etc.
            } else {
                Toast.makeText(getApplicationContext(), "Failed to submit report!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void showStep1() {
        step1Container.setVisibility(View.VISIBLE);
        step2Container.setVisibility(View.GONE);
        progressBarSteps.setProgress(50);
    }


    private void showStep2() {
        step1Container.setVisibility(View.GONE);
        step2Container.setVisibility(View.VISIBLE);
        progressBarSteps.setProgress(100);
    }


    // Handle Image Picker result and display image name
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            // For simplicity, use the last path segment as the image name
            String imageName = imageUri.getLastPathSegment();
            tvSelectedImage.setText("Selected: " + imageName);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // Simple email validation
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
