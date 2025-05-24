package com.example.smishingdetectionapp.ui.Register;

public class SignupResponse {
    private boolean success;
    private String message;

    // Constructor, Getter, Setter methods
    public SignupResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
