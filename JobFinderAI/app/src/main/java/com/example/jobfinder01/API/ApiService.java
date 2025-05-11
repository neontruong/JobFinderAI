package com.example.jobfinder01.API;

import com.example.jobfinder01.CV.CV;
import com.example.jobfinder01.CV.CVCreateResponse;
import com.example.jobfinder01.CV.CVResponse;
import com.example.jobfinder01.Models.Job;
import com.example.jobfinder01.Models.Profile;
import com.example.jobfinder01.Models.UserProfile;
import com.example.jobfinder01.ui.home.apply.ApplicationRequest;
import com.example.jobfinder01.ui.jobapplications.JobApplication;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // Đăng ký tài khoản
    @Headers({"Accept: application/json"})
    @POST("api/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

    // Đăng nhập
    @POST("api/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    // Quên mật khẩu
    @POST("api/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    // Đặt lại mật khẩu
    @POST("api/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest request);

    // Tạo Job (cần Token Authorization)
    @POST("api/jobs")
    Call<Void> createJob(@Body Job job);

    @GET("api/jobs") // Đảm bảo endpoint khớp với Laravel
    Call<List<Job>> getAllJobs();

    @GET("api/jobs/{id}")
    Call<Job> getJobById(@Path("id") int id);

    @POST("api/profile/save")
    Call<Void> saveProfile(@Path("userId") int userId, @Body Profile profile, @Body String imageUri);

    @GET("api/profile/check/{userId}")
    Call<ProfileStatus> checkProfile(@Path("userId") int userId);

    @GET("api/profile/{userId}")
    Call<UserProfile> getProfile(@Path("userId") int userId);

    @Multipart
    @POST("api/profile/update")
    Call<Void> updateProfile(
            @Part("user_id") RequestBody userId,
            @Part MultipartBody.Part avatar,
            @Part("full_name") RequestBody fullName,
            @Part("job_title") RequestBody jobTitle,
            @Part("email") RequestBody email,
            @Part("phone") RequestBody phone,
            @Part("skills") RequestBody skills,
            @Part("experience") RequestBody experience,
            @Part("preference") RequestBody preference,
            @Part("location") RequestBody location,
            @Part("salary") RequestBody salary,
            @Part("education") RequestBody education,
            @Part("industry") RequestBody industry
    );

    @POST("api/cvs")
    Call<CVCreateResponse> createCV(@Body CV cv);

    @GET("api/cvs")
    Call<CVResponse> getCVs();

    @GET("api/cvs/{id}")
    Call<CVCreateResponse> getCVDetail(@Path("id") int cvId);

    @POST("api/applications")
    Call<Void> applyForJob(@Body ApplicationRequest request);
    @GET("api/user-applications")
    Call<List<JobApplication>> getUserApplications();


}
