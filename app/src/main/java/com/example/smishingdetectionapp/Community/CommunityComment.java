package com.example.smishingdetectionapp.Community;

public class CommunityComment {
    private int commentId;
    private int postId;
    private String user;
    private String date;
    private String commentText;

    public CommunityComment(int commentId, int postId, String user, String date, String commentText) {
        this.commentId = commentId;
        this.postId = postId;
        this.user = user;
        this.date = date;
        this.commentText = commentText;
    }

    public CommunityComment(int postId, String user, String date, String commentText) {
        this.postId = postId;
        this.user = user;
        this.date = date;
        this.commentText = commentText;
    }

    public int getCommentId() { return commentId; }
    public int getPostId() { return postId; }
    public String getUser() { return user; }
    public String getDate() { return date; }
    public String getCommentText() { return commentText; }
}