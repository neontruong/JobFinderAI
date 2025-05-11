package com.example.jobfinder01.Chat;

import android.net.Uri;
import com.example.jobfinder01.Models.Job;
import java.util.List;

public class Message {
    private String text;
    private boolean isUser;
    private Uri fileUri;
    private List<Job> jobs;
    private String jobTitle; // Thêm để lưu tiêu đề công việc
    private String contactEmail; // Thêm để lưu email liên hệ

    public Message(String text, boolean isUser, Uri fileUri) {
        this.text = text;
        this.isUser = isUser;
        this.fileUri = fileUri;
    }

    public Message(String text, boolean isUser, Uri fileUri, List<Job> jobs) {
        this.text = text;
        this.isUser = isUser;
        this.fileUri = fileUri;
        this.jobs = jobs;
    }

    public Message(String text, boolean isUser, Uri fileUri, String jobTitle, String contactEmail) {
        this.text = text;
        this.isUser = isUser;
        this.fileUri = fileUri;
        this.jobTitle = jobTitle;
        this.contactEmail = contactEmail;
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public boolean isJobMessage() {
        return jobs != null && !jobs.isEmpty();
    }

    public boolean isFileMessage() {
        return fileUri != null;
    }
}