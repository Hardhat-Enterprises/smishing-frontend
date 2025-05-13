package com.example.smishingdetectionapp;

import java.util.ArrayList;
import java.util.List;

public class FeedbackMemoryStore {
    private static final List<String> feedbackList = new ArrayList<>();

    public static void addFeedback(String feedback) {
        feedbackList.add(feedback);
    }

    public static List<String> getFeedbackHistory() {
        return new ArrayList<>(feedbackList);
    }
}
