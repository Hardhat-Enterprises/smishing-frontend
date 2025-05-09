package com.example.smishingdetectionapp;

public class CommunityPost {
    public String username; //profile should have username in the future or to use the name as display
    public String posttitle;
    public String postdescription;
    public int likes;
    public int comments;

    public CommunityPost(String username, String posttitle, String postdescription, int likes, int comments) {
        this.username = username;
        this.posttitle = posttitle;
        this.postdescription = postdescription;
        this.likes = likes;
        this.comments = comments;
    }

    // allow data to be retrieved by other classes
    public String getUsername() {
        return username;
    }

    public String getPosttitle() {
        return posttitle;
    }

    public String getPostdescription() {
        return postdescription;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }
}