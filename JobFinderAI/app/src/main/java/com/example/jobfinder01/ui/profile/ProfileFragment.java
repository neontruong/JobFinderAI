package com.example.jobfinder01.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.jobfinder01.API.ApiClient;
import com.example.jobfinder01.API.ApiService;
import com.example.jobfinder01.CV.ManageCVActivity;
import com.example.jobfinder01.LoginActivity;
import com.example.jobfinder01.Models.UserProfile;
import com.example.jobfinder01.R;
import com.example.jobfinder01.ui.home.RecentJobsActivity;
import com.example.jobfinder01.ui.home.SavedJobsActivity;
import com.google.android.material.button.MaterialButton;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private CircleImageView profileImage;
    private TextView tvUsername, tvJobTitle, tvEmail, tvPhone, tvSkills, tvExperience, tvPreference, tvLocation, tvSalary, tvEducation, tvIndustry;
    private Button btnEditProfile, btnManageCV, btnLogout;
    private MaterialButton btnViewSavedJobs, btnViewRecentJobs;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        profileImage = view.findViewById(R.id.profile_image);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvJobTitle = view.findViewById(R.id.tvJobTitle);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvSkills = view.findViewById(R.id.tvSkills);
        tvExperience = view.findViewById(R.id.tvExperience);
        tvPreference = view.findViewById(R.id.tvPreference);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvSalary = view.findViewById(R.id.tvSalary);
        tvEducation = view.findViewById(R.id.tvEducation);
        tvIndustry = view.findViewById(R.id.tvIndustry);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnManageCV = view.findViewById(R.id.btnManageCV);
        btnViewSavedJobs = view.findViewById(R.id.btnViewSavedJobs);
        btnViewRecentJobs = view.findViewById(R.id.btnViewRecentJobs);
        btnLogout = view.findViewById(R.id.btnLogout);

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        if (sharedPreferences == null) {
            Toast.makeText(getContext(), "Không thể truy cập SharedPreferences", Toast.LENGTH_SHORT).show();
            return view;
        }

        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            return view;
        }

        loadProfile(userId);

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        btnManageCV.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ManageCVActivity.class);
            startActivity(intent);
        });

        btnViewSavedJobs.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SavedJobsActivity.class);
            startActivity(intent);
        });

        btnViewRecentJobs.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RecentJobsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear().apply(); // Chỉ xóa thông tin đăng nhập (userId, v.v.), không xóa SavedJobs và RecentJobs
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void loadProfile(int userId) {
        ApiService apiService = ApiClient.getAuthClient(getContext()).create(ApiService.class);
        Call<UserProfile> call = apiService.getProfile(userId);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();

                    if (profile.getAvatar() != null && getContext() != null) {
                        Glide.with(getContext())
                                .load(profile.getAvatar())
                                .placeholder(R.drawable.ic_profile)
                                .into(profileImage);
                    }
                    tvUsername.setText(profile.getFullName() != null ? profile.getFullName() : "N/A");
                    tvJobTitle.setText(profile.getJobTitle() != null ? profile.getJobTitle() : "N/A");
                    tvEmail.setText(profile.getEmail() != null ? profile.getEmail() : "N/A");
                    tvPhone.setText(profile.getPhone() != null ? profile.getPhone() : "N/A");
                    tvSkills.setText(profile.getSkills() != null ? profile.getSkills() : "N/A");
                    tvExperience.setText(profile.getExperience() != null ? profile.getExperience() : "N/A");
                    tvPreference.setText(profile.getPreference() != null ? profile.getPreference() : "N/A");
                    tvLocation.setText(profile.getLocation() != null ? profile.getLocation() : "N/A");
                    tvSalary.setText(profile.getSalary() != null ? String.format("%,.0f", Double.parseDouble(profile.getSalary())) : "N/A");
                    tvEducation.setText(profile.getEducation() != null ? profile.getEducation() : "N/A");
                    tvIndustry.setText(profile.getIndustry() != null ? profile.getIndustry() : "N/A");
                } else {
                    Toast.makeText(getContext(), "Không thể tải thông tin hồ sơ - Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            loadProfile(userId);
        }
    }
}