package com.example.manejoimpresora.Onboarding;

public class OnboardingItem {
    private String description;
    private int image;
    private String title;

    public int getImage() {
        return this.image;
    }

    public void setImage(int i) {
        this.image = i;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }
}
