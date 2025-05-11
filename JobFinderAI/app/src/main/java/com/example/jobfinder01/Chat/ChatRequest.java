package com.example.jobfinder01.Chat;

public class ChatRequest {
    private String message;
    private boolean is_user;
    private String file_uri;

    public ChatRequest(String message, boolean isUser, String fileUri) {
        this.message = message;
        this.is_user = isUser;
        this.file_uri = fileUri;
    }
}