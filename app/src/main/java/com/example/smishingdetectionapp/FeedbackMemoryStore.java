package com.example.smishingdetectionapp;

import java.util.ArrayList;
import java.util.List;

public class FeedbackMemoryStore {
    private static final List<String> feedbackList = new ArrayList<>();

    // Add new feedback
    public static void addFeedback(String feedback) {
        feedbackList.add(feedback);
    }

    // Retrieve all feedback entries
    public static List<String> getFeedbackHistory() {
        return new ArrayList<>(feedbackList); // return a copy to avoid direct modification
    }
    public static void updateFeedback(int index, String newEntry) {
        if (index >= 0 && index < feedbackList.size()) {
            feedbackList.set(index, newEntry);
        }
    }
    // Remove specific feedback entry
    public static void removeFeedback(String feedback) {
        feedbackList.remove(feedback); // match and remove from memory
    }
    // Update a specific feedback entry
    public static void updateFeedback(String oldEntry, String newEntry) {
        int index = feedbackList.indexOf(oldEntry);
        if (index != -1) {
            feedbackList.set(index, newEntry);
        }
    }
    // Clear all feedbacks if needed (optional utility)
    public static void clearAllFeedback() {
        feedbackList.clear();
    }
}