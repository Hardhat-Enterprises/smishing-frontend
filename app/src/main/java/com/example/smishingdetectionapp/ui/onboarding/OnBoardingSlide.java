package com.example.smishingdetectionapp.ui.onboarding;

public class OnBoardingSlide {
    private int image;
    private String title;
    private String description;

    public OnBoardingSlide(int image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }



}
