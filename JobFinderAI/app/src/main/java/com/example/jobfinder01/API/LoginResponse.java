package com.example.jobfinder01.API;

public class LoginResponse {
    private String message;
    private String token;
    private String role;
    private int userId; // Thêm userId

    // Getters
    public int getUserId() { return userId; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public String getRole() { return role; }

    // Setters
    public void setMessage(String message) { this.message = message; }
    public void setToken(String token) { this.token = token; }
    public void setRole(String role) { this.role = role; }
}