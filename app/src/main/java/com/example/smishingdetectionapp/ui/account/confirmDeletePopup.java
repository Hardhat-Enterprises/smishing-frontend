// confirmDeletePopup.java
package com.example.smishingdetectionapp.ui.account;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.smishingdetectionapp.R;

public class confirmDeletePopup extends Dialog {
    private Context mContext;

    public confirmDeletePopup(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_delete, null);
        setContentView(view);

        Button yesButton = view.findViewById(R.id.yesButton);
        Button noButton = view.findViewById(R.id.noButton);

        yesButton.setOnClickListener(v -> {
            dismiss();
            if (mContext instanceof AccountActivity) {
                Intent intent = new Intent(mContext, PopupDEL.class);
                mContext.startActivity(intent);
            }
        });

        noButton.setOnClickListener(v -> dismiss());
    }
}