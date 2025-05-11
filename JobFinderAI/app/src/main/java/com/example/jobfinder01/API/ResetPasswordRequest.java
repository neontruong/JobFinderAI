package com.example.jobfinder01.API;

public class ResetPasswordRequest {
    private String token;
    private String email;
    private String password;
    private String password_confirmation;

    public ResetPasswordRequest(String token, String email, String password, String password_confirmation) {
        this.token = token;
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPassword_confirmation() {
        return password_confirmation;
    }
}
