package com.example.smishingdetectionapp.ui.Register;

public class VerificationResponse {
    private boolean success;
    private String message;

    public VerificationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

