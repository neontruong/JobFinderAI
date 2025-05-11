package com.example.jobfinder01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jobfinder01.API.ApiClient;
import com.example.jobfinder01.API.ApiService;
import com.example.jobfinder01.API.LoginRequest;
import com.example.jobfinder01.API.LoginResponse;
import com.example.jobfinder01.API.ProfileStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail, editPassword;
    private Button btnLogin, btnRegister;
    private ImageButton btnTogglePassword;
    private TextView tvForgotPassword;
    private CheckBox checkboxRememberMe;
    private SharedPreferences sharedPreferences;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Khởi tạo các view
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        checkboxRememberMe = findViewById(R.id.checkboxRememberMe);

        // Kiểm tra và điền thông tin nếu đã chọn "Nhớ mật khẩu"
        boolean isRemembered = sharedPreferences.getBoolean("rememberMe", false);
        if (isRemembered) {
            String savedEmail = sharedPreferences.getString("saved_email", "");
            String savedPassword = sharedPreferences.getString("saved_password", "");
            editEmail.setText(savedEmail);
            editPassword.setText(savedPassword);
            checkboxRememberMe.setChecked(true);
        }

        // Kiểm tra trạng thái đăng nhập
        if (isLoggedIn()) {
            checkProfileStatus();
            return;
        }

        // Xử lý toggle password visibility
        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
                isPasswordVisible = false;
            } else {
                editPassword.setTransformationMethod(null);
                btnTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                isPasswordVisible = true;
            }
            editPassword.setSelection(editPassword.getText().length());
        });

        // Xử lý nút Login
        btnLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        // Xử lý nút Register
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // Xử lý Forgot Password
        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        // Xử lý đăng nhập admin
        if (email.equals("admin@jobfinder.com") && password.equals("Admin123")) {
            Toast.makeText(this, "Đăng nhập thành công với tài khoản Admin!", Toast.LENGTH_SHORT).show();
            saveLoginInfo("fake_admin_token", "admin", email, 1);
            saveRememberMe(email, password);
            ApiClient.resetAuthClient();
            checkProfileStatus();
            return;
        }

        // Gọi API đăng nhập
        ApiService apiService = ApiClient.getPublicClient().create(ApiService.class);
        LoginRequest request = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.loginUser(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    String token = loginResponse.getToken();
                    String role = loginResponse.getRole();
                    int userId = loginResponse.getUserId();
                    saveLoginInfo(token, role, email, userId);
                    saveRememberMe(email, password);
                    ApiClient.resetAuthClient();
                    checkProfileStatus();
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("LoginError", "Code: " + response.code() + " - Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginError", "Failure: " + t.getMessage());
            }
        });
    }

    private void saveLoginInfo(String token, String role, String email, int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.putString("user_role", role);
        editor.putString("email", email);
        editor.putInt("user_id", userId);
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    private void saveRememberMe(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (checkboxRememberMe.isChecked()) {
            editor.putString("saved_email", email);
            editor.putString("saved_password", password);
            editor.putBoolean("rememberMe", true);
        } else {
            editor.remove("saved_email");
            editor.remove("saved_password");
            editor.putBoolean("rememberMe", false);
        }
        editor.apply();
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void checkProfileStatus() {
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getAuthClient(this).create(ApiService.class);
        Call<ProfileStatus> call = apiService.checkProfile(userId);
        call.enqueue(new Callback<ProfileStatus>() {
            @Override
            public void onResponse(Call<ProfileStatus> call, Response<ProfileStatus> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isProfileCompleted()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, CompleteProfileActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Không thể kiểm tra trạng thái profile", Toast.LENGTH_SHORT).show();
                    Log.e("ProfileError", "Code: " + response.code() + " - Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ProfileStatus> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProfileError", "Failure: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing() && !isChangingConfigurations()) {
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
            if (!isLoggedIn) {
                return;
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", false);
            editor.apply();
        }
    }
}