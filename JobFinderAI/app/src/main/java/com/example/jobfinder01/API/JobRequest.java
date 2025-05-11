package com.example.jobfinder01.API;

import com.google.gson.annotations.SerializedName;

public class JobRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("company")
    private String company;

    @SerializedName("location")
    private String location;

    @SerializedName("salary")
    private double salary;

    @SerializedName("user_id")
    private int userId;

    // Constructor không tham số (yêu cầu của GSON khi deserialize)
    public JobRequest() {}

    // Constructor đầy đủ để tạo đối tượng
    public JobRequest(String title, String description, String company, String location, double salary, int userId) {
        this.title = title;
        this.description = description;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.userId = userId;
    }

    // Getters và Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}