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
import androidx.fragment.app.Fragment;
import com.example.jobfinder01.CV.ManageCVActivity;
import com.example.jobfinder01.LoginActivity;
import com.example.jobfinder01.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends Fragment {

    private SharedPreferences sharedPreferences;

    public ProfileActivity() {
        // Required empty public constructor
    }

    public static ProfileActivity newInstance() {
        return new ProfileActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", requireActivity().MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Không ánh xạ Toolbar hoặc gọi setSupportActionBar để tránh trùng lặp
        // Toolbar sẽ chỉ hiển thị như một phần của giao diện Fragment

        // Ánh xạ các view
        CircleImageView profileImage = view.findViewById(R.id.profile_image);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        TextView tvJobTitle = view.findViewById(R.id.tvJobTitle);
        TextView tvSkills = view.findViewById(R.id.tvSkills);
        TextView tvExperience = view.findViewById(R.id.tvExperience);
        TextView tvPreference = view.findViewById(R.id.tvPreference);
        TextView tvLocation = view.findViewById(R.id.tvLocation);
        TextView tvSalary = view.findViewById(R.id.tvSalary);
        TextView tvEducation = view.findViewById(R.id.tvEducation);
        TextView tvIndustry = view.findViewById(R.id.tvIndustry);

        Button btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        Button btnManageCV = view.findViewById(R.id.btnManageCV);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Load dữ liệu từ SharedPreferences
        tvUsername.setText(sharedPreferences.getString("username", "Nguyễn Văn A"));
        tvEmail.setText(sharedPreferences.getString("email", "email@example.com"));
        tvPhone.setText(sharedPreferences.getString("phone", "0123-456-789"));
        tvJobTitle.setText(sharedPreferences.getString("job_title", "Software Engineer"));
        tvSkills.setText(sharedPreferences.getString("skills", "N/A"));
        tvExperience.setText(sharedPreferences.getString("experience", "N/A"));
        tvPreference.setText(sharedPreferences.getString("preference", "N/A"));
        tvLocation.setText(sharedPreferences.getString("location", "N/A"));
        tvSalary.setText(sharedPreferences.getString("salary", "N/A"));
        tvEducation.setText(sharedPreferences.getString("education", "N/A"));
        tvIndustry.setText(sharedPreferences.getString("industry", "N/A"));

        // Load ảnh đại diện (nếu có)
        String avatarUrl = sharedPreferences.getString("avatar", null);
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            try {
                profileImage.setImageURI(Uri.parse(avatarUrl));
            } catch (Exception e) {
                profileImage.setImageResource(R.drawable.ic_profile);
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_profile);
        }

        // Xử lý các nút
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnManageCV.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManageCVActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear().apply();
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProfileData();
    }

    private void updateProfileData() {
        View view = getView();
        if (view != null) {
            CircleImageView profileImage = view.findViewById(R.id.profile_image);
            TextView tvUsername = view.findViewById(R.id.tvUsername);
            TextView tvEmail = view.findViewById(R.id.tvEmail);
            TextView tvPhone = view.findViewById(R.id.tvPhone);
            TextView tvJobTitle = view.findViewById(R.id.tvJobTitle);
            TextView tvSkills = view.findViewById(R.id.tvSkills);
            TextView tvExperience = view.findViewById(R.id.tvExperience);
            TextView tvPreference = view.findViewById(R.id.tvPreference);
            TextView tvLocation = view.findViewById(R.id.tvLocation);
            TextView tvSalary = view.findViewById(R.id.tvSalary);
            TextView tvEducation = view.findViewById(R.id.tvEducation);
            TextView tvIndustry = view.findViewById(R.id.tvIndustry);

            tvUsername.setText(sharedPreferences.getString("username", "Nguyễn Văn A"));
            tvEmail.setText(sharedPreferences.getString("email", "email@example.com"));
            tvPhone.setText(sharedPreferences.getString("phone", "0123-456-789"));
            tvJobTitle.setText(sharedPreferences.getString("job_title", "Software Engineer"));
            tvSkills.setText(sharedPreferences.getString("skills", "N/A"));
            tvExperience.setText(sharedPreferences.getString("experience", "N/A"));
            tvPreference.setText(sharedPreferences.getString("preference", "N/A"));
            tvLocation.setText(sharedPreferences.getString("location", "N/A"));
            tvSalary.setText(sharedPreferences.getString("salary", "N/A"));
            tvEducation.setText(sharedPreferences.getString("education", "N/A"));
            tvIndustry.setText(sharedPreferences.getString("industry", "N/A"));

            String avatarUrl = sharedPreferences.getString("avatar", null);
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                try {
                    profileImage.setImageURI(Uri.parse(avatarUrl));
                } catch (Exception e) {
                    profileImage.setImageResource(R.drawable.ic_profile);
                }
            } else {
                profileImage.setImageResource(R.drawable.ic_profile);
            }
        }
    }
}