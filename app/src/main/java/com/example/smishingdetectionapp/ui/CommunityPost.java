package com.example.smishingdetectionapp.ui;

import java.util.List;

public class CommunityPost {
    private String title;
    private String content;
    private String author;
    private List<String> comments; // List of comments for the post

    public CommunityPost(String title, String content, String author, List<String> comments) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.comments = comments;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getComments() {
        return comments;
    }

    public String getAuthor() {
        return author;
    } //the author field is being used in the CommunityPost constructor, can be used in the future

}
