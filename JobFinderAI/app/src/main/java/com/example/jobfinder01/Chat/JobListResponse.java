package com.example.jobfinder01.Chat;

import com.example.jobfinder01.Models.Job;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JobListResponse {
    @SerializedName("type")
    private String type;

    @SerializedName("message")
    private String message;

    @SerializedName("jobs")
    private List<Job> jobs;

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public List<Job> getJobs() {
        return jobs;
    }
}