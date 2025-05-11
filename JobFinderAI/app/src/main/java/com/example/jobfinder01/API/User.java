package com.example.jobfinder01.API;

public class User {
    private int id;
    private String name;
    private String email;
    private String password; // Thêm mật khẩu

    // Constructor
    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Tạo tài khoản admin cứng với mật khẩu
    public static final User ADMIN_USER = new User(1, "Admin", "admin@jobfinder.com", "Admin123");
}
