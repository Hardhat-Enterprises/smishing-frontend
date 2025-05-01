package com.example.smishingdetectionapp.detections;

public class DetectionItem {
    String phoneNumber;
    String message;
    String date;

    public DetectionItem(String phoneNumber, String message, String date) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.date = date;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public String getMessage() { return message; }
    public String getDate() { return date; }
}

