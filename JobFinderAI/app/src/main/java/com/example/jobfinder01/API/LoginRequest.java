package com.example.jobfinder01.API;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Kiểm tra đăng nhập với tài khoản admin cứng
    public boolean isValidAdmin() {
        return email.equals(User.ADMIN_USER.getEmail()) && password.equals(User.ADMIN_USER.getPassword());
    }
}
