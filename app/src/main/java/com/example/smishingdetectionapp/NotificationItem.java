package com.example.smishingdetectionapp;

public class NotificationItem {
    private String title;
    private String message;

    // ✅ Required no-argument constructor for Gson
    public NotificationItem() {
    }

    public NotificationItem(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
