package com.example.jobfinder01.ui.jobapplications;

public class JobApplication {
    private int id;
    private int userId;
    private int jobId;
    private int cvId;
    private String createdAt; // Dùng created_at làm applied_date
    private String updatedAt;
    private String status; // Thêm status
    private Job job; // Dùng Job từ package com.example.jobfinder01.ui.jobapplications
    private CV cv;

    // Getters và setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public int getCvId() { return cvId; }
    public void setCvId(int cvId) { this.cvId = cvId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public CV getCv() { return cv; }
    public void setCv(CV cv) { this.cv = cv; }

    // Các phương thức tiện ích để lấy dữ liệu hiển thị
    public String getJobTitle() {
        return job != null ? job.getTitle() : "Không có thông tin";
    }

    public String getCompany() {
        return job != null ? job.getCompany() : "Không có thông tin";
    }

    public String getAppliedDate() {
        return createdAt != null ? createdAt : "Không có thông tin";
    }
}