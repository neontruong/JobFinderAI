package com.example.jobfinder01.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.jobfinder01.API.ApiClient;
import com.example.jobfinder01.API.ApiService;
import com.example.jobfinder01.Models.UserProfile;
import com.example.jobfinder01.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private CircleImageView profileImage;
    private EditText etFullName, etJobTitle, etEmail, etPhone, etSkills, etExperience, etPreference, etLocation, etSalary, etEducation, etIndustry;
    private Button btnSaveProfile;
    private Uri imageUri;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImage = findViewById(R.id.profileImage);
        etFullName = findViewById(R.id.etFullName);
        etJobTitle = findViewById(R.id.etJobTitle);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etSkills = findViewById(R.id.etSkills);
        etExperience = findViewById(R.id.etExperience);
        etPreference = findViewById(R.id.etPreference);
        etLocation = findViewById(R.id.etLocation);
        etSalary = findViewById(R.id.etSalary);
        etEducation = findViewById(R.id.etEducation);
        etIndustry = findViewById(R.id.etIndustry);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProfile();

        profileImage.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                openImagePicker();
            } else {
                requestStoragePermission();
            }
        });

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else { // Android 12 trở xuống
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    Manifest.permission.READ_MEDIA_IMAGES
            }, STORAGE_PERMISSION_CODE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES
            }, STORAGE_PERMISSION_CODE);
        } else { // Android 12 trở xuống
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Quyền truy cập bộ nhớ bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = Intent.createChooser(galleryIntent, "Chọn ảnh");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        startActivityForResult(chooser, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                profileImage.setImageURI(imageUri);
            } else {
                Toast.makeText(this, "Không thể chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProfile() {
        ApiService apiService = ApiClient.getAuthClient(this).create(ApiService.class);
        Call<UserProfile> call = apiService.getProfile(userId);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();

                    if (profile.getAvatar() != null) {
                        Glide.with(EditProfileActivity.this)
                                .load(profile.getAvatar())
                                .placeholder(R.drawable.ic_profile)
                                .into(profileImage);
                    }
                    etFullName.setText(profile.getFullName());
                    etJobTitle.setText(profile.getJobTitle());
                    etEmail.setText(profile.getEmail());
                    etPhone.setText(profile.getPhone());
                    etSkills.setText(profile.getSkills());
                    etExperience.setText(profile.getExperience());
                    etPreference.setText(profile.getPreference());
                    etLocation.setText(profile.getLocation());
                    etSalary.setText(profile.getSalary());
                    etEducation.setText(profile.getEducation());
                    etIndustry.setText(profile.getIndustry());
                } else {
                    Toast.makeText(EditProfileActivity.this, "Không thể tải thông tin profile - Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String salaryStr = etSalary.getText().toString().trim();
        String preferenceStr = etPreference.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền họ tên và email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra Preference để tránh nhầm lẫn với Salary
        if (!preferenceStr.isEmpty()) {
            try {
                Double.parseDouble(preferenceStr.replaceAll("[^0-9.]", ""));
                Toast.makeText(this, "Trường Sở thích nên nhập văn bản, không phải số. Nếu bạn muốn nhập mức lương, hãy dùng trường Mức lương.", Toast.LENGTH_LONG).show();
            } catch (NumberFormatException e) {
                // Không làm gì, đây là trường văn bản
            }
        }

        // Xử lý Salary
        RequestBody salaryBody;
        if (salaryStr.isEmpty() || salaryStr.equals("N/A")) {
            salaryBody = RequestBody.create(MediaType.parse("text/plain"), "");
        } else {
            try {
                String cleanSalary = salaryStr.replaceAll("[^0-9.]", "");
                Log.d("EditProfile", "Raw salary: '" + salaryStr + "'");
                Log.d("EditProfile", "Cleaned salary: '" + cleanSalary + "'");

                if (cleanSalary.isEmpty()) {
                    salaryBody = RequestBody.create(MediaType.parse("text/plain"), "");
                } else {
                    String[] parts = cleanSalary.split("\\.");
                    if (parts.length > 2) {
                        throw new NumberFormatException("Giá trị không hợp lệ: Nhiều hơn một dấu chấm");
                    }
                    Double.parseDouble(cleanSalary);
                    salaryBody = RequestBody.create(MediaType.parse("text/plain"), cleanSalary);
                }
            } catch (NumberFormatException e) {
                Log.e("EditProfile", "Error parsing salary: " + e.getMessage());
                Toast.makeText(this, "Mức lương phải là số hợp lệ (ví dụ: 50000 hoặc 50000.00)", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Dùng getAuthClient vì API update profile cần xác thực
        ApiService apiService = ApiClient.getAuthClient(this).create(ApiService.class);

        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
        RequestBody fullNameBody = RequestBody.create(MediaType.parse("text/plain"), fullName);
        RequestBody jobTitle = RequestBody.create(MediaType.parse("text/plain"), etJobTitle.getText().toString().trim());
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), etPhone.getText().toString().trim());
        RequestBody skills = RequestBody.create(MediaType.parse("text/plain"), etSkills.getText().toString().trim());
        RequestBody experience = RequestBody.create(MediaType.parse("text/plain"), etExperience.getText().toString().trim());
        RequestBody preference = RequestBody.create(MediaType.parse("text/plain"), preferenceStr);
        RequestBody location = RequestBody.create(MediaType.parse("text/plain"), etLocation.getText().toString().trim());
        RequestBody education = RequestBody.create(MediaType.parse("text/plain"), etEducation.getText().toString().trim());
        RequestBody industry = RequestBody.create(MediaType.parse("text/plain"), etIndustry.getText().toString().trim());

        MultipartBody.Part avatarPart = null;
        if (imageUri != null) {
            try {
                // Đọc dữ liệu từ Uri dưới dạng InputStream
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream == null) {
                    Toast.makeText(this, "Không thể đọc file ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo file tạm thời để lưu dữ liệu từ InputStream
                File file = new File(getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();

                // Tạo RequestBody và MultipartBody.Part từ file tạm
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                avatarPart = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

                // Xóa file tạm sau khi sử dụng
                file.deleteOnExit();
            } catch (IOException e) {
                Log.e("EditProfile", "Error processing image: " + e.getMessage());
                Toast.makeText(this, "Lỗi khi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Call<Void> call = apiService.updateProfile(
                userIdBody, avatarPart, fullNameBody, jobTitle, emailBody, phone, skills, experience, preference, location, salaryBody, education, industry
        );
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile đã được cập nhật", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Thông báo cho ProfileFragment làm mới dữ liệu
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có chi tiết lỗi";
                        Log.e("UpdateProfile", "Error Code: " + response.code() + ", Error: " + errorBody);
                        if (errorBody.contains("Validation failed")) {
                            if (errorBody.contains("salary")) {
                                Toast.makeText(EditProfileActivity.this, "Mức lương phải là số hợp lệ (ví dụ: 50000 hoặc 50000.00)", Toast.LENGTH_LONG).show();
                            } else if (errorBody.contains("email")) {
                                Toast.makeText(EditProfileActivity.this, "Email đã tồn tại, vui lòng chọn email khác", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại - Mã: " + response.code() + " - Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        Log.e("UpdateProfile", "Error reading response: " + e.getMessage());
                        Toast.makeText(EditProfileActivity.this, "Lỗi đọc phản hồi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UpdateProfile", "Network error: " + t.getMessage());
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}