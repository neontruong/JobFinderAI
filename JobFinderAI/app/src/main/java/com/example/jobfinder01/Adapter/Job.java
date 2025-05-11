package com.example.jobfinder01.Adapter;

public class Job {
    private long id;
    private String title;
    private String company;
    private String location;
    private String salary;
    private String logoUrl;
    private String contactEmail;

    public Job(long id, String title, String company, String location, String salary, String logoUrl, String contactEmail) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.logoUrl = logoUrl;
        this.contactEmail = contactEmail;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public String getSalary() {
        return salary;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getContactEmail() {
        return contactEmail;
    }
}