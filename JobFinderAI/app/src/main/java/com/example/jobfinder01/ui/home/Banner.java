package com.example.jobfinder01.ui.home;

public class Banner {
    private String title;
    private String subtext;
    private int iconResId;

    public Banner(String title, String subtext, int iconResId) {
        this.title = title;
        this.subtext = subtext;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtext() {
        return subtext;
    }

    public int getIconResId() {
        return iconResId;
    }
}