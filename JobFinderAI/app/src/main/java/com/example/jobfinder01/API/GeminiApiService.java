package com.example.jobfinder01.API;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GeminiApiService {
    @Headers("Content-Type: application/json")
    @POST("v1/models/gemini-1.5-flash:generateContent?key=AIzaSyApa4iZOUOkEd7m-ik_C7CdT-JNUTWjKKY")
    Call<com.example.jobfinder01.ResponseBody> getResponse(@Body okhttp3.RequestBody request);
}