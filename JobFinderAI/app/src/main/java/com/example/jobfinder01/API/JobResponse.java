package com.example.jobfinder01.API;

import com.google.gson.annotations.SerializedName;

public class JobResponse {
    @SerializedName("data")
    private Job data;

    // Getter và Setter cho data
    public Job getData() { return data; }
    public void setData(Job data) { this.data = data; }

    // Nested class Job để đại diện cho dữ liệu công việc
    public static class Job {
        private int id;
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
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;

        // Getters và Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

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

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }
}