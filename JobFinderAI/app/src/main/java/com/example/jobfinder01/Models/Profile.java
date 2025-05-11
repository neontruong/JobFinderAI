package com.example.jobfinder01.Models;

public class Profile {
    private String fullName, jobTitle, email, phone, skills, location, salary;

    public Profile(String fullName, String jobTitle, String email, String phone, String skills, String location, String salary) {
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.location = location;
        this.salary = salary;
    }

    // Getter và Setter
    public String getFullName() { return fullName; }
    public String getJobTitle() { return jobTitle; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getSkills() { return skills; }
    public String getLocation() { return location; }
    public String getSalary() { return salary; }
}