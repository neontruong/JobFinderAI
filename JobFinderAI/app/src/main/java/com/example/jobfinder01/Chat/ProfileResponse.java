package com.example.jobfinder01.Chat;

import com.example.jobfinder01.Models.UserProfile;

public class ProfileResponse {
    private String message;
    private UserProfile data;

    public String getMessage() {
        return message;
    }
    public UserProfile getData() {
        return data;
    }
}