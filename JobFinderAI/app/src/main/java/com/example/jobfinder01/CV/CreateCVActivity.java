package com.example.jobfinder01.CV;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobfinder01.API.ApiClient;
import com.example.jobfinder01.API.ApiService;
import com.example.jobfinder01.CV.CV;
import com.example.jobfinder01.CV.CVCreateResponse;
import com.example.jobfinder01.CV.EducationExperience;
import com.example.jobfinder01.CV.Skill;
import com.example.jobfinder01.CV.WorkExperience;
import com.example.jobfinder01.R;
//import com.example.jobfinder01.CV.ManageCVActivity; // Import đúng package
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class CreateCVActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private CircleImageView ivAvatar;
    private EditText etFullName, etJobTitle, etPhone, etEmail, etWorkArea, etAddress, etDateOfBirth, etLinkedin, etObjective, etAdditionalInfo;
    private Spinner spinnerEducation, spinnerExperience, spinnerGender, spinnerSalary, spinnerHobbies;
    private RecyclerView rvWorkExperience, rvSkills, rvEducationExperience;
    private Button btnAddWorkExperience, btnAddSkill, btnAddEducation, btnSaveCV;

    private List<WorkExperience> workExperienceList = new ArrayList<>();
    private List<Skill> skillList = new ArrayList<>();
    private List<EducationExperience> educationExperienceList = new ArrayList<>();
    private WorkExperienceAdapter workExperienceAdapter;
    private SkillAdapter skillAdapter;
    private EducationExperienceAdapter educationExperienceAdapter;

    private Uri avatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cv);

        initViews();
        setupSpinners();
        setupRecyclerViews();
        setupListeners();
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        etFullName = findViewById(R.id.etFullName);
        etJobTitle = findViewById(R.id.etJobTitle);
        spinnerEducation = findViewById(R.id.spinnerEducation);
        spinnerExperience = findViewById(R.id.spinnerExperience);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etWorkArea = findViewById(R.id.etWorkArea);
        etAddress = findViewById(R.id.etAddress);
        spinnerGender = findViewById(R.id.spinnerGender);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        spinnerSalary = findViewById(R.id.spinnerSalary);
        etLinkedin = findViewById(R.id.etLinkedin);
        etObjective = findViewById(R.id.etObjective);
        rvWorkExperience = findViewById(R.id.rvWorkExperience);
        btnAddWorkExperience = findViewById(R.id.btnAddWorkExperience);
        rvSkills = findViewById(R.id.rvSkills);
        btnAddSkill = findViewById(R.id.btnAddSkill);
        rvEducationExperience = findViewById(R.id.rvEducationExperience);
        btnAddEducation = findViewById(R.id.btnAddEducation);
        spinnerHobbies = findViewById(R.id.spinnerHobbies);
        etAdditionalInfo = findViewById(R.id.etAdditionalInfo);
        btnSaveCV = findViewById(R.id.btnSaveCV);
    }

    private void setupSpinners() {
        ArrayAdapter<String> educationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Tiểu học", "Trung học cơ sở", "Trung học phổ thông", "Trường dạy nghề", "Trường trung cấp",
                        "Cao đẳng", "Đại học", "Thạc sĩ", "Tiến sĩ"});
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEducation.setAdapter(educationAdapter);

        ArrayAdapter<String> experienceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"1 năm", "2 năm", "3 năm", "4 năm", "5 năm", "6 năm", "7 năm", "8 năm", "9 năm", "10 năm", "10 năm trở lên"});
        experienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExperience.setAdapter(experienceAdapter);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Nam", "Nữ", "Khác"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<String> salaryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"5-10 triệu", "10-15 triệu", "15-20 triệu", "20-30 triệu", "Trên 30 triệu"});
        salaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSalary.setAdapter(salaryAdapter);

        ArrayAdapter<String> hobbiesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Đọc sách", "Du lịch", "Thể thao", "Nấu ăn", "Chơi game", "Xem phim"});
        hobbiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHobbies.setAdapter(hobbiesAdapter);
    }

    private void setupRecyclerViews() {
        workExperienceAdapter = new WorkExperienceAdapter(workExperienceList);
        rvWorkExperience.setLayoutManager(new LinearLayoutManager(this));
        rvWorkExperience.setAdapter(workExperienceAdapter);

        skillAdapter = new SkillAdapter(skillList);
        rvSkills.setLayoutManager(new LinearLayoutManager(this));
        rvSkills.setAdapter(skillAdapter);

        educationExperienceAdapter = new EducationExperienceAdapter(educationExperienceList);
        rvEducationExperience.setLayoutManager(new LinearLayoutManager(this));
        rvEducationExperience.setAdapter(educationExperienceAdapter);
    }

    private void setupListeners() {
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog(etDateOfBirth));

        btnAddWorkExperience.setOnClickListener(v -> showAddWorkExperienceDialog());
        btnAddSkill.setOnClickListener(v -> showAddSkillDialog());
        btnAddEducation.setOnClickListener(v -> showAddEducationDialog());
        btnSaveCV.setOnClickListener(v -> saveCV());
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    editText.setText(sdf.format(calendar.getTime()));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showAddWorkExperienceDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_work_experience, null);
        EditText etJobTitle = dialogView.findViewById(R.id.etJobTitle);
        EditText etCompanyName = dialogView.findViewById(R.id.etCompanyName);
        EditText etStartDate = dialogView.findViewById(R.id.etStartDate);
        EditText etEndDate = dialogView.findViewById(R.id.etEndDate);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);

        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));

        new MaterialAlertDialogBuilder(this)
                .setTitle("Thêm kinh nghiệm làm việc")
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    WorkExperience work = new WorkExperience(
                            etJobTitle.getText().toString(),
                            etCompanyName.getText().toString(),
                            etStartDate.getText().toString(),
                            etEndDate.getText().toString(),
                            etDescription.getText().toString()
                    );
                    workExperienceList.add(work);
                    workExperienceAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAddSkillDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_skill, null);
        EditText etSkillName = dialogView.findViewById(R.id.etSkillName);
        EditText etSkillDescription = dialogView.findViewById(R.id.etSkillDescription);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Thêm kỹ năng")
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    Skill skill = new Skill(
                            etSkillName.getText().toString(),
                            etSkillDescription.getText().toString()
                    );
                    skillList.add(skill);
                    skillAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAddEducationDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_education, null);
        EditText etSchoolName = dialogView.findViewById(R.id.etSchoolName);
        EditText etMajor = dialogView.findViewById(R.id.etMajor);
        EditText etStartDate = dialogView.findViewById(R.id.etStartDate);
        EditText etEndDate = dialogView.findViewById(R.id.etEndDate);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);

        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));

        new MaterialAlertDialogBuilder(this)
                .setTitle("Thêm kinh nghiệm học vấn")
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    EducationExperience education = new EducationExperience(
                            etSchoolName.getText().toString(),
                            etMajor.getText().toString(),
                            etStartDate.getText().toString(),
                            etEndDate.getText().toString(),
                            etDescription.getText().toString()
                    );
                    educationExperienceList.add(education);
                    educationExperienceAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveCV() {
        // Lấy user_id từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userIdInt = prefs.getInt("user_id", 0); // Sửa getLong thành getInt

        if (userIdInt == 0) {
            Toast.makeText(this, "Không tìm thấy user ID. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra các trường bắt buộc
        String fullName = etFullName.getText().toString().trim();
        String jobTitle = etJobTitle.getText().toString().trim();
        String highestEducation = spinnerEducation.getSelectedItem() != null ? spinnerEducation.getSelectedItem().toString() : "";
        String experience = spinnerExperience.getSelectedItem() != null ? spinnerExperience.getSelectedItem().toString() : "";
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String workArea = etWorkArea.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem() != null ? spinnerGender.getSelectedItem().toString() : "";
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String salary = spinnerSalary.getSelectedItem() != null ? spinnerSalary.getSelectedItem().toString() : "";

        // Kiểm tra các trường bắt buộc
        if (fullName.isEmpty()) {
            etFullName.setError("Họ và tên là bắt buộc");
            etFullName.requestFocus();
            return;
        }
        if (jobTitle.isEmpty()) {
            etJobTitle.setError("Nghề nghiệp ứng tuyển là bắt buộc");
            etJobTitle.requestFocus();
            return;
        }
        if (highestEducation.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn bằng cấp cao nhất", Toast.LENGTH_SHORT).show();
            return;
        }
        if (experience.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn kinh nghiệm", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty()) {
            etPhone.setError("Số điện thoại là bắt buộc");
            etPhone.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email là bắt buộc");
            etEmail.requestFocus();
            return;
        }
        if (workArea.isEmpty()) {
            etWorkArea.setError("Khu vực làm việc là bắt buộc");
            etWorkArea.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            etAddress.setError("Địa chỉ chi tiết là bắt buộc");
            etAddress.requestFocus();
            return;
        }
        if (gender.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dateOfBirth.isEmpty()) {
            etDateOfBirth.setError("Ngày tháng năm sinh là bắt buộc");
            etDateOfBirth.requestFocus();
            return;
        }
        if (salary.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn mức lương", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng CV
        CV cv = new CV();
        cv.setUserId((long) userIdInt); // Ép kiểu int sang long
        cv.setAvatar(avatarUri != null ? avatarUri.toString() : null);
        cv.setFullName(fullName);
        cv.setJobTitle(jobTitle);
        cv.setHighestEducation(highestEducation);
        cv.setExperience(experience);
        cv.setPhone(phone);
        cv.setEmail(email);
        cv.setWorkArea(workArea);
        cv.setAddress(address);
        cv.setGender(gender);
        cv.setDateOfBirth(dateOfBirth);
        cv.setSalary(salary);
        cv.setLinkedin(etLinkedin.getText().toString().trim());
        cv.setObjective(etObjective.getText().toString().trim());
        cv.setWorkExperience(workExperienceList);
        cv.setSkills(skillList);
        cv.setEducationExperience(educationExperienceList);
        cv.setHobbies(spinnerHobbies.getSelectedItem() != null ? spinnerHobbies.getSelectedItem().toString() : "");
        cv.setAdditionalInfo(etAdditionalInfo.getText().toString().trim());

        // Gửi API
        ApiService apiService = ApiClient.getAuthClient(this).create(ApiService.class);
        Call<CVCreateResponse> call = apiService.createCV(cv);
        call.enqueue(new Callback<CVCreateResponse>() {
            @Override
            public void onResponse(Call<CVCreateResponse> call, Response<CVCreateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateCVActivity.this, "CV đã được lưu thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Chỉ gọi finish() khi lưu thành công
                } else {
                    try {
                        ResponseBody errorBody = response.errorBody();
                        if (errorBody != null) {
                            String errorMessage = errorBody.string();
                            Toast.makeText(CreateCVActivity.this, "Lỗi khi lưu CV: " + errorMessage, Toast.LENGTH_LONG).show();
                            Log.e("ApiError", "Response: " + errorMessage);
                        } else {
                            Toast.makeText(CreateCVActivity.this, "Lỗi khi lưu CV: " + response.code(), Toast.LENGTH_SHORT).show();
                            Log.e("ApiError", "Response code: " + response.code());
                        }
                    } catch (IOException e) {
                        Toast.makeText(CreateCVActivity.this, "Lỗi khi đọc phản hồi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ApiError", "IOException: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CVCreateResponse> call, Throwable t) {
                Toast.makeText(CreateCVActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ApiError", "Failure: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            avatarUri = data.getData();
            ivAvatar.setImageURI(avatarUri);
        }
    }

    // Sửa lại các Adapter
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
            holder.tvJobTitle.setText(work.getJobTitle() != null ? work.getJobTitle() : "");
            holder.tvCompanyName.setText(work.getCompanyName() != null ? work.getCompanyName() : "");
            holder.tvDuration.setText((work.getStartDate() != null ? work.getStartDate() : "") + " - " + (work.getEndDate() != null ? work.getEndDate() : ""));
            holder.tvDescription.setText(work.getDescription() != null ? work.getDescription() : "");
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
            holder.tvSkillName.setText(skill.getName() != null ? skill.getName() : "");
            holder.tvSkillDescription.setText(skill.getDescription() != null ? skill.getDescription() : "");
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
            holder.tvSchoolName.setText(education.getSchoolName() != null ? education.getSchoolName() : "");
            holder.tvMajor.setText(education.getMajor() != null ? education.getMajor() : "");
            holder.tvDuration.setText((education.getStartDate() != null ? education.getStartDate() : "") + " - " + (education.getEndDate() != null ? education.getEndDate() : ""));
            holder.tvDescription.setText(education.getDescription() != null ? education.getDescription() : "");
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