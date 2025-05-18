package com.example.smishingdetectionapp.Community;

public class CommunityPost {
    private int id;
    public String username;//profile should have username in the future or to use the name as display
    private String date;
    public String posttitle;
    public String postdescription;
    public int likes;
    public int comments;

    public CommunityPost(int id, String username, String date, String posttitle, String postdescription, int likes, int comments) {
        this.id = id;
        this.username = username;
        this.date = date;
        this.posttitle = posttitle;
        this.postdescription = postdescription;
        this.likes = likes;
        this.comments = comments;
    }

    // allow data to be retrieved by other classes
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
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