package com.example.jobfinder01.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Job implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("company")
    private String company;

    @SerializedName("location")
    private String location;

    @SerializedName("salary")
    private double salary;

    @SerializedName("logo_url")
    private String logoUrl;

    @SerializedName("description")
    private String description;

    @SerializedName("contact_phone")
    private String contactPhone;

    @SerializedName("contact_email")
    private String contactEmail;

    @SerializedName("company_description")
    private String companyDescription;

    @SerializedName("requirements")
    private String requirements;

    @SerializedName("benefits")
    private String benefits;

    @SerializedName("posted_at")
    private String postedAt;

    // Constructor đầy đủ (13 tham số)
    public Job(int id, String title, String company, String location, double salary, String logoUrl, String description,
               String contactPhone, String contactEmail, String companyDescription, String requirements, String benefits, String postedAt) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.logoUrl = logoUrl;
        this.description = description;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.companyDescription = companyDescription;
        this.requirements = requirements;
        this.benefits = benefits;
        this.postedAt = postedAt;
    }

    // Constructor ngắn gọn (5 tham số)
    public Job(String title, String company, String location, double salary, String logoUrl) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.logoUrl = logoUrl;
    }

    // Constructor mới (4 tham số kiểu String) để khớp với HomeFragment
    public Job(String title, String company, String location, String salary) {
        this.title = title;
        this.company = company;
        this.location = location;
        try {
            // Xử lý salary từ String thành double (lấy số đầu tiên hoặc trung bình)
            String[] parts = salary.split("-");
            if (parts.length == 2) {
                double min = Double.parseDouble(parts[0].replaceAll("[^0-9.]", ""));
                double max = Double.parseDouble(parts[1].replaceAll("[^0-9.]", ""));
                this.salary = (min + max) / 2; // Trung bình của khoảng lương
            } else {
                this.salary = Double.parseDouble(salary.replaceAll("[^0-9.]", ""));
            }
        } catch (NumberFormatException e) {
            this.salary = 0.0; // Giá trị mặc định nếu lỗi
        }
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public double getSalary() { return salary; }
    public String getLogoUrl() { return logoUrl; }
    public String getDescription() { return description; }
    public String getContactPhone() { return contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public String getCompanyDescription() { return companyDescription; }
    public String getRequirements() { return requirements; }
    public String getBenefits() { return benefits; }
    public String getPostedAt() { return postedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCompany(String company) { this.company = company; }
    public void setLocation(String location) { this.location = location; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public void setCompanyDescription(String companyDescription) { this.companyDescription = companyDescription; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public void setBenefits(String benefits) { this.benefits = benefits; }
    public void setPostedAt(String postedAt) { this.postedAt = postedAt; }
}