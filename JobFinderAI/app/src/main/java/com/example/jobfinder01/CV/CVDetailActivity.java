package com.example.jobfinder01.CV;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobfinder01.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;

public class CVDetailActivity extends AppCompatActivity {

    private CircleImageView ivAvatar;
    private TextView tvFullName, tvJobTitle, tvHighestEducation, tvExperience, tvPhone, tvEmail, tvWorkArea, tvAddress, tvGender, tvDateOfBirth, tvSalary, tvLinkedin, tvObjective, tvHobbies, tvAdditionalInfo, tvCreatedAt;
    private RecyclerView rvWorkExperience, rvSkills, rvEducationExperience;
    private WorkExperienceAdapter workExperienceAdapter;
    private SkillAdapter skillAdapter;
    private EducationExperienceAdapter educationExperienceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cv_detail);

        // Khởi tạo các view
        initViews();

        // Lấy dữ liệu từ Intent
        CV cv = (CV) getIntent().getSerializableExtra("cv");
        if (cv == null) {
            Log.e("CVDetailActivity", "CV data is null");
            finish();
            return;
        }
        displayCVDetails(cv);
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        tvFullName = findViewById(R.id.tvFullName);
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvHighestEducation = findViewById(R.id.tvHighestEducation);
        tvExperience = findViewById(R.id.tvExperience);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvWorkArea = findViewById(R.id.tvWorkArea);
        tvAddress = findViewById(R.id.tvAddress);
        tvGender = findViewById(R.id.tvGender);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvSalary = findViewById(R.id.tvSalary);
        tvLinkedin = findViewById(R.id.tvLinkedin);
        tvObjective = findViewById(R.id.tvObjective);
        tvHobbies = findViewById(R.id.tvHobbies);
        tvAdditionalInfo = findViewById(R.id.tvAdditionalInfo);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);

        rvWorkExperience = findViewById(R.id.rvWorkExperience);
        rvSkills = findViewById(R.id.rvSkills);
        rvEducationExperience = findViewById(R.id.rvEducationExperience);

        // Kiểm tra null trước khi thiết lập RecyclerView
        if (rvWorkExperience == null || rvSkills == null || rvEducationExperience == null) {
            Log.e("CVDetailActivity", "One or more RecyclerViews not found in layout");
            return;
        }

        // Thiết lập RecyclerView
        setupRecyclerViews();
    }

    private void setupRecyclerViews() {
        // Work Experience RecyclerView
        workExperienceAdapter = new WorkExperienceAdapter(new ArrayList<>());
        rvWorkExperience.setLayoutManager(new LinearLayoutManager(this));
        rvWorkExperience.setAdapter(workExperienceAdapter);

        // Skills RecyclerView
        skillAdapter = new SkillAdapter(new ArrayList<>());
        rvSkills.setLayoutManager(new LinearLayoutManager(this));
        rvSkills.setAdapter(skillAdapter);

        // Education Experience RecyclerView
        educationExperienceAdapter = new EducationExperienceAdapter(new ArrayList<>());
        rvEducationExperience.setLayoutManager(new LinearLayoutManager(this));
        rvEducationExperience.setAdapter(educationExperienceAdapter);
    }

    private void displayCVDetails(CV cv) {
        // Hiển thị ảnh đại diện
        if (cv.getAvatar() != null && !cv.getAvatar().isEmpty()) {
            ivAvatar.setImageURI(Uri.parse(cv.getAvatar()));
        }

        // Hiển thị thông tin cơ bản
        tvFullName.setText("Họ và tên: " + (cv.getFullName() != null ? cv.getFullName() : "N/A"));
        tvJobTitle.setText("Vị trí ứng tuyển: " + (cv.getJobTitle() != null ? cv.getJobTitle() : "N/A"));
        tvHighestEducation.setText("Bằng cấp cao nhất: " + (cv.getHighestEducation() != null ? cv.getHighestEducation() : "N/A"));
        tvExperience.setText("Kinh nghiệm: " + (cv.getExperience() != null ? cv.getExperience() : "N/A"));
        tvPhone.setText("Số điện thoại: " + (cv.getPhone() != null ? cv.getPhone() : "N/A"));
        tvEmail.setText("Email: " + (cv.getEmail() != null ? cv.getEmail() : "N/A"));
        tvWorkArea.setText("Khu vực làm việc: " + (cv.getWorkArea() != null ? cv.getWorkArea() : "N/A"));
        tvAddress.setText("Địa chỉ: " + (cv.getAddress() != null ? cv.getAddress() : "N/A"));
        tvGender.setText("Giới tính: " + (cv.getGender() != null ? cv.getGender() : "N/A"));
        tvDateOfBirth.setText("Ngày sinh: " + (cv.getDateOfBirth() != null ? cv.getDateOfBirth() : "N/A"));
        tvSalary.setText("Mức lương: " + (cv.getSalary() != null ? cv.getSalary() : "N/A"));
        tvLinkedin.setText("LinkedIn: " + (cv.getLinkedin() != null ? cv.getLinkedin() : "N/A"));

        // Hiển thị thông tin hồ sơ
        tvObjective.setText("Mục tiêu: " + (cv.getObjective() != null ? cv.getObjective() : "N/A"));

        // Hiển thị kinh nghiệm làm việc
        if (cv.getWorkExperience() != null && !cv.getWorkExperience().isEmpty()) {
            workExperienceAdapter = new WorkExperienceAdapter(cv.getWorkExperience());
            rvWorkExperience.setAdapter(workExperienceAdapter);
        }

        // Hiển thị kỹ năng
        if (cv.getSkills() != null && !cv.getSkills().isEmpty()) {
            skillAdapter = new SkillAdapter(cv.getSkills());
            rvSkills.setAdapter(skillAdapter);
        }

        // Hiển thị kinh nghiệm học vấn
        if (cv.getEducationExperience() != null && !cv.getEducationExperience().isEmpty()) {
            educationExperienceAdapter = new EducationExperienceAdapter(cv.getEducationExperience());
            rvEducationExperience.setAdapter(educationExperienceAdapter);
        }

        // Hiển thị sở thích và thông tin bổ sung
        tvHobbies.setText("Sở thích: " + (cv.getHobbies() != null ? cv.getHobbies() : "N/A"));
        tvAdditionalInfo.setText("Thông tin bổ sung: " + (cv.getAdditionalInfo() != null ? cv.getAdditionalInfo() : "N/A"));

        // Hiển thị thời gian tạo
        String createdAt = cv.getCreatedAt();
        if (createdAt != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(createdAt);
                tvCreatedAt.setText("Ngày tạo: " + outputFormat.format(date));
            } catch (Exception e) {
                tvCreatedAt.setText("Ngày tạo: " + createdAt);
            }
        } else {
            tvCreatedAt.setText("Ngày tạo: N/A");
        }
    }

    // Adapter cho WorkExperience
    private class WorkExperienceAdapter extends RecyclerView.Adapter<WorkExperienceAdapter.ViewHolder> {
        private List<WorkExperience> list;

        public WorkExperienceAdapter(List<WorkExperience> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_experience, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            WorkExperience work = list.get(position);
            holder.tvJobTitle.setText(work.getJobTitle() != null ? work.getJobTitle() : "N/A");
            holder.tvCompanyName.setText(work.getCompanyName() != null ? work.getCompanyName() : "N/A");
            holder.tvDuration.setText((work.getStartDate() != null ? work.getStartDate() : "N/A") + " - " + (work.getEndDate() != null ? work.getEndDate() : "N/A"));
            holder.tvDescription.setText(work.getDescription() != null ? work.getDescription() : "N/A");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvJobTitle, tvCompanyName, tvDuration, tvDescription;

            public ViewHolder(View itemView) {
                super(itemView);
                tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
                tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
                tvDuration = itemView.findViewById(R.id.tvDuration);
                tvDescription = itemView.findViewById(R.id.tvDescription);
            }
        }
    }

    // Adapter cho Skill
    private class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {
        private List<Skill> list;

        public SkillAdapter(List<Skill> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skill, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Skill skill = list.get(position);
            holder.tvSkillName.setText(skill.getName() != null ? skill.getName() : "N/A");
            holder.tvSkillDescription.setText(skill.getDescription() != null ? skill.getDescription() : "N/A");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSkillName, tvSkillDescription;

            public ViewHolder(View itemView) {
                super(itemView);
                tvSkillName = itemView.findViewById(R.id.tvSkillName);
                tvSkillDescription = itemView.findViewById(R.id.tvSkillDescription);
            }
        }
    }

    // Adapter cho EducationExperience
    private class EducationExperienceAdapter extends RecyclerView.Adapter<EducationExperienceAdapter.ViewHolder> {
        private List<EducationExperience> list;

        public EducationExperienceAdapter(List<EducationExperience> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_education_experience, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            EducationExperience education = list.get(position);
            holder.tvSchoolName.setText(education.getSchoolName() != null ? education.getSchoolName() : "N/A");
            holder.tvMajor.setText(education.getMajor() != null ? education.getMajor() : "N/A");
            holder.tvDuration.setText((education.getStartDate() != null ? education.getStartDate() : "N/A") + " - " + (education.getEndDate() != null ? education.getEndDate() : "N/A"));
            holder.tvDescription.setText(education.getDescription() != null ? education.getDescription() : "N/A");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSchoolName, tvMajor, tvDuration, tvDescription;

            public ViewHolder(View itemView) {
                super(itemView);
                tvSchoolName = itemView.findViewById(R.id.tvSchoolName);
                tvMajor = itemView.findViewById(R.id.tvMajor);
                tvDuration = itemView.findViewById(R.id.tvDuration);
                tvDescription = itemView.findViewById(R.id.tvDescription);
            }
        }
    }
}