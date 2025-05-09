// AccountActivity.java
package com.example.smishingdetectionapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.SharedActivity;

public class AccountActivity extends SharedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button to go back to settings page
        ImageButton account_back = findViewById(R.id.account_back);
        account_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // Opens the password change window
        Button password_changeBtn = findViewById(R.id.passwordBtn);
        password_changeBtn.setOnClickListener(v -> {
            PopupPW bottomSheet = new PopupPW();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        Button pin_changeBtn = findViewById(R.id.pinBtn);
        pin_changeBtn.setOnClickListener(v -> {
            PopupPIN bottomSheet = new PopupPIN();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });
        // Opens the email change window
        Button email_changeBtn = findViewById(R.id.emailBtn);
        email_changeBtn.setOnClickListener(v -> {
            PopupEmail bottomSheet = new PopupEmail();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        // Opens the sign out window
        Button sign_outBtn = findViewById(R.id.buttonSignOut);
        sign_outBtn.setOnClickListener(v -> {
            PopupSO bottomSheet = new PopupSO();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });

        // Opens the delete account confirmation popup
        Button delete_accBtn = findViewById(R.id.account_delete);
        delete_accBtn.setOnClickListener(v -> {
            confirmDeletePopup confirmDeletePopup = new confirmDeletePopup(AccountActivity.this);
            confirmDeletePopup.show();
        });

        // Opens the phone number change window
        Button change_phone_numberBtn = findViewById(R.id.phoneBtn);
        change_phone_numberBtn.setOnClickListener(v -> {
            PopupPN bottomSheet = new PopupPN();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });
    }

}