package com.example.jobfinder01.API;

// Model cho response (dữ liệu trả về từ server)
public class RegisterResponse {
    private String message;
    private User user;
    private String token;

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}
