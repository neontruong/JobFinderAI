package com.example.jobfinder01;

import android.content.Context;
import android.widget.Toast;
import com.example.jobfinder01.API.ApiClient;
import com.example.jobfinder01.API.ApiService;
import com.example.jobfinder01.API.ForgotPasswordRequest;
import com.example.jobfinder01.API.ForgotPasswordResponse;
import com.example.jobfinder01.API.ResetPasswordRequest;
import com.example.jobfinder01.API.ResetPasswordResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordResetManager {
    private Context context;

    public PasswordResetManager(Context context) {
        this.context = context;
    }

    public void requestPasswordReset(String email, Runnable onSuccess, Runnable onFailure) {
        ApiService apiService = ApiClient.getPublicClient().create(ApiService.class);
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        apiService.forgotPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else {
                    Toast.makeText(context, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                onFailure.run();
            }
        });
    }

    public void resetPassword(String email, String token, String password, String passwordConfirmation, Runnable onSuccess, Runnable onFailure) {
        ApiService apiService = ApiClient.getPublicClient().create(ApiService.class);
        ResetPasswordRequest request = new ResetPasswordRequest(token, email, password, passwordConfirmation);

        apiService.resetPassword(request).enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                } else {
                    String errorMessage = "Lỗi: " + response.message();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                onFailure.run();
            }
        });
    }
}