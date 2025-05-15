package com.example.smishingdetectionapp;
//leonora start
public class Message {
    private String text;
    private boolean isSmishing;

    public Message(String text, boolean isSmishing) {
        this.text = text;
        this.isSmishing = isSmishing;
    }

    public String getText() {
        return text;
    }

    public boolean isSmishing() {
        return isSmishing;
    }
}
//leonora end