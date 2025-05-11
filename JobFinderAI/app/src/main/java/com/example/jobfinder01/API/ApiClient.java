package com.example.jobfinder01.API;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit publicRetrofit = null;
    private static Retrofit authRetrofit = null;

    // Client cho các API không cần xác thực
    public static Retrofit getPublicClient() {
        if (publicRetrofit == null) {
            publicRetrofit = new Retrofit.Builder()
                    .baseUrl("http://172.16.69.76:8000/") // Thêm /api/
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return publicRetrofit;
    }

    // Client cho các API cần xác thực
    public static Retrofit getAuthClient(Context context) {
        if (authRetrofit == null) {
            SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("auth_token", "");

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Log.d("ApiClient", "Request URL: " + original.url()); // Log để kiểm tra
                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("Authorization", "Bearer " + token)
                                    .header("Accept", "application/json");
                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            authRetrofit = new Retrofit.Builder()
                    .baseUrl("http://172.16.69.76:8000/") // Thêm /api/
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return authRetrofit;
    }

    public static void resetAuthClient() {
        authRetrofit = null;
    }

    public static void resetPublicClient() {
        publicRetrofit = null;
    }
}