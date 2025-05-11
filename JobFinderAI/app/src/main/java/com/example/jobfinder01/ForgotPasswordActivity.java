package com.example.jobfinder01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jobfinder01.PasswordResetManager;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText otpEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button sendOtpButton;
    private Button resetPasswordButton;
    private PasswordResetManager resetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        otpEditText = findViewById(R.id.otpEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        resetManager = new PasswordResetManager(this);

        sendOtpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            resetManager.requestPasswordReset(email,
                    () -> {
                        otpEditText.setVisibility(View.VISIBLE);
                        passwordEditText.setVisibility(View.VISIBLE);
                        confirmPasswordEditText.setVisibility(View.VISIBLE);
                        resetPasswordButton.setVisibility(View.VISIBLE);
                        sendOtpButton.setEnabled(false);
                    },
                    () -> {});
        });

        resetPasswordButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String token = otpEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (email.isEmpty() || token.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            resetManager.resetPassword(email, token, password, confirmPassword,
                    () -> {
                        Toast.makeText(this, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    },
                    () -> {});
        });
    }
}