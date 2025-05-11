package com.example.jobfinder01.CV;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class EducationExperience implements Serializable {
    @SerializedName("school_name")
    private String schoolName;

    @SerializedName("major")
    private String major;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("description")
    private String description;

    public EducationExperience() {}

    public EducationExperience(String schoolName, String major, String startDate, String endDate, String description) {
        this.schoolName = schoolName;
        this.major = major;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}