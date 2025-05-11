package com.example.jobfinder01.CV;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class CV implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private long userId;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("job_title")
    private String jobTitle;

    @SerializedName("highest_education")
    private String highestEducation;

    @SerializedName("experience")
    private String experience;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("work_area")
    private String workArea;

    @SerializedName("address")
    private String address;

    @SerializedName("gender")
    private String gender;

    @SerializedName("date_of_birth")
    private String dateOfBirth;

    @SerializedName("salary")
    private String salary;

    @SerializedName("linkedin")
    private String linkedin;

    @SerializedName("objective")
    private String objective;

    @SerializedName("work_experience")
    private List<WorkExperience> workExperience;

    @SerializedName("skills")
    private List<Skill> skills;

    @SerializedName("education_experience")
    private List<EducationExperience> educationExperience;

    @SerializedName("hobbies")
    private String hobbies;

    @SerializedName("additional_info")
    private String additionalInfo;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("user")
    private User user;

    public CV() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getHighestEducation() { return highestEducation; }
    public void setHighestEducation(String highestEducation) { this.highestEducation = highestEducation; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWorkArea() { return workArea; }
    public void setWorkArea(String workArea) { this.workArea = workArea; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }

    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }

    public List<WorkExperience> getWorkExperience() { return workExperience; }
    public void setWorkExperience(List<WorkExperience> workExperience) { this.workExperience = workExperience; }

    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }

    public List<EducationExperience> getEducationExperience() { return educationExperience; }
    public void setEducationExperience(List<EducationExperience> educationExperience) { this.educationExperience = educationExperience; }

    public String getHobbies() { return hobbies; }
    public void setHobbies(String hobbies) { this.hobbies = hobbies; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}