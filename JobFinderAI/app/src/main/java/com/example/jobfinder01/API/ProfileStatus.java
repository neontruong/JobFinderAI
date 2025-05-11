package com.example.jobfinder01.API;

import com.google.gson.annotations.SerializedName;

public class ProfileStatus {
    @SerializedName("is_profile_completed") // Sửa tên key cho khớp với API
    private int profileCompleted;

    public boolean isProfileCompleted() {
        return profileCompleted == 1;
    }
}