package com.example.jobfinder01.ui.home.apply;

import com.google.gson.annotations.SerializedName;

public class ApplicationRequest {
    @SerializedName("job_id")
    private int jobId;

    @SerializedName("cv_id")
    private int cvId;

    public ApplicationRequest(int jobId, int cvId) {
        this.jobId = jobId;
        this.cvId = cvId;
    }

    public int getJobId() { return jobId; }
    public int getCvId() { return cvId; }
}