package com.example.jobfinder01.Chat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatApiService {
    @GET("api/chat/history")
    Call<ChatHistoryResponse> getChatHistory();

    @POST("api/chat/history")
    Call<ChatResponse> saveChat(@Body ChatRequest request);

    @GET("api/chat/profile")
    Call<ProfileResponse> getProfile();


    @GET("api/cover-letter/{job_id}")
    Call<CoverLetterResponse> getCoverLetter(@Path("job_id") String jobId);
}