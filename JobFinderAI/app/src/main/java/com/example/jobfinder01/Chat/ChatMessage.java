package com.example.jobfinder01.Chat;

import android.net.Uri;

public class ChatMessage {
    private long id;
    private long user_id;
    private String message;
    private int is_user; // Đổi từ boolean sang int
    private String file_uri;
    private String created_at;

    // Getters và setters
    public String getMessage() { return message; }

    public boolean isUser() {
        return is_user == 1; // Chuyển đổi 0/1 thành false/true
    }

    public Uri getFileUriAsUri() {
        return file_uri != null ? Uri.parse(file_uri) : null;
    }
}