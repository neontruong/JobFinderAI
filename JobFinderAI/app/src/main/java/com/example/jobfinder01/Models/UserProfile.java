package com.example.jobfinder01.Models;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    @SerializedName("userId")
    private int userId;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("job_title")
    private String jobTitle;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("skills")
    private String skills;

    @SerializedName("experience")
    private String experience;

    @SerializedName("preference")
    private String preference;

    @SerializedName("location")
    private String location;

    @SerializedName("salary")
    private String salary;

    @SerializedName("education")
    private String education;

    @SerializedName("industry")
    private String industry;

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getFullName() {
        return fullName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getSkills() {
        return skills;
    }

    public String getExperience() {
        return experience;
    }

    public String getPreference() {
        return preference;
    }

    public String getLocation() {
        return location;
    }

    public String getSalary() {
        return salary;
    }

    public String getEducation() {
        return education;
    }

    public String getIndustry() {
        return industry;
    }
}