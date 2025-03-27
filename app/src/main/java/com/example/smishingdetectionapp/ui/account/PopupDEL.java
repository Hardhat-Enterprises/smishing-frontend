package com.example.smishingdetectionapp.ui.account;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;

import java.util.Calendar;
import java.util.Date;

public class PopupDEL extends AppCompatActivity {

    private EditText passwordEditText;
    private Button confirmDelYesBtn, confirmDelNoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_delete_account);

        passwordEditText = findViewById(R.id.del_accPW);
        confirmDelYesBtn = findViewById(R.id.confirmDelYesBtn);
        confirmDelNoBtn = findViewById(R.id.confirmDelNoBtn);

        confirmDelNoBtn.setOnClickListener(view -> {
            Intent intent = new Intent(com.example.smishingdetectionapp.ui.account.PopupDEL.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        confirmDelYesBtn.setOnClickListener(view -> handleAccountDeletion());

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                confirmDelYesBtn.setEnabled(!TextUtils.isEmpty(s));
            }
        });
    }

    private void handleAccountDeletion() {
        String enteredPassword = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(enteredPassword)) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPasswordValid(enteredPassword)) {
            disableAccountFor30Days();
        } else {
            Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPasswordValid(String password) {
        String storedPassword = getStoredPassword(); // Get the stored password (you can set a mock password)
        return password.equals(storedPassword); // Compare with the entered password
    }
    private String getStoredPassword() {
        return "userPassword"; // Placeholder: Replace this with actual stored password or DB retrieval logic
    }

    private void disableAccountFor30Days() {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date deletionDate = calendar.getTime();

        saveAccountStatus("disabled");
        saveDeletionDate(deletionDate);

        Toast.makeText(this, "Account will be deleted on " + deletionDate, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(com.example.smishingdetectionapp.ui.account.PopupDEL.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void saveAccountStatus(String status) {
        // Placeholder for saving account status in the database
    }

    private void saveDeletionDate(Date deletionDate) {
        // Placeholder for saving the deletion date in the database
    }
}
