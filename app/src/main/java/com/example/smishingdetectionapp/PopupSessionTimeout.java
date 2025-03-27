package com.example.smishingdetectionapp;

import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.smishingdetectionapp.ui.login.LoginActivity;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PopupSessionTimeout extends DialogFragment {

    private static final int POPUP_TIMEOUT_MS = 30000; // 30 seconds timer for the popup timeout
    private Handler popupHandler;
    private Runnable popupTimeoutRunnable;
    private SessionTimeoutListener listener;

    // Interface to handle user response
    public interface SessionTimeoutListener {
        void onContinueSession();
    }

    public void setSessionTimeoutListener(SessionTimeoutListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_session_timeout, container, false);

        Button continueButton = v.findViewById(R.id.continueBtn);
        continueButton.setOnClickListener(v1 -> {
            if (listener != null) {
                listener.onContinueSession();
            }
            dismiss();
        });

        // Logout button setup
        Button logoutButton = v.findViewById(R.id.logoutBtn);
        logoutButton.setOnClickListener(v2 -> {

            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dismiss();
        });


        popupHandler = new Handler();
        popupHandler.postDelayed(this::onPopupTimeout, POPUP_TIMEOUT_MS);

        return v;
    }

    private void onPopupTimeout() {
        // If statement to logout automatically if the popup times out
        if (getActivity() != null && !getActivity().isFinishing()) {
            logout();
        }
    }

    private void logout() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (popupHandler != null) {
            popupHandler.removeCallbacksAndMessages(null);
        }
    }
}
