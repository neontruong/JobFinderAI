package com.example.jobfinder01;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class CompleteProfileActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_complete_profile);

        // Ánh xạ
        profileImage = findViewById(R.id.profile_image);
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

        // Lấy userId từ SharedPreferences
        userId = getLoggedInUserId();
        if (userId == -1) {
            finish();
            return;
        }

        // Xử lý chọn ảnh
        profileImage.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                openImagePicker();
            } else {
                requestStoragePermission();
            }
        });

        // Xử lý lưu profile
        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
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
            profileImage.setImageURI(imageUri);
        }
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền họ tên và email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm log để kiểm tra dữ liệu
        Log.d("CompleteProfile", "user_id: " + userId);
        Log.d("CompleteProfile", "full_name: " + fullName);
        Log.d("CompleteProfile", "email: " + email);
        Log.d("CompleteProfile", "job_title: " + etJobTitle.getText().toString().trim());
        Log.d("CompleteProfile", "phone: " + etPhone.getText().toString().trim());
        Log.d("CompleteProfile", "skills: " + etSkills.getText().toString().trim());
        Log.d("CompleteProfile", "experience: " + etExperience.getText().toString().trim());
        Log.d("CompleteProfile", "preference: " + etPreference.getText().toString().trim());
        Log.d("CompleteProfile", "location: " + etLocation.getText().toString().trim());
        Log.d("CompleteProfile", "salary: " + etSalary.getText().toString().trim());
        Log.d("CompleteProfile", "education: " + etEducation.getText().toString().trim());
        Log.d("CompleteProfile", "industry: " + etIndustry.getText().toString().trim());
        Log.d("CompleteProfile", "avatar: " + (imageUri != null ? imageUri.toString() : "null"));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.16.69.76:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JobApi jobApi = retrofit.create(JobApi.class);

        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
        RequestBody fullNameBody = RequestBody.create(MediaType.parse("text/plain"), fullName);
        RequestBody jobTitle = RequestBody.create(MediaType.parse("text/plain"), etJobTitle.getText().toString().trim());
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), etPhone.getText().toString().trim());
        RequestBody skills = RequestBody.create(MediaType.parse("text/plain"), etSkills.getText().toString().trim());
        RequestBody experience = RequestBody.create(MediaType.parse("text/plain"), etExperience.getText().toString().trim());
        RequestBody preference = RequestBody.create(MediaType.parse("text/plain"), etPreference.getText().toString().trim());
        RequestBody location = RequestBody.create(MediaType.parse("text/plain"), etLocation.getText().toString().trim());
        RequestBody salary = RequestBody.create(MediaType.parse("text/plain"), etSalary.getText().toString().trim());
        RequestBody education = RequestBody.create(MediaType.parse("text/plain"), etEducation.getText().toString().trim());
        RequestBody industry = RequestBody.create(MediaType.parse("text/plain"), etIndustry.getText().toString().trim());

        MultipartBody.Part avatarPart = null;
        if (imageUri != null) {
            try {
                // Đọc dữ liệu ảnh từ Uri bằng ContentResolver
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream == null) {
                    Toast.makeText(this, "Không thể đọc file ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                // Kiểm tra kích thước file (giới hạn 2MB)
                long fileSizeInKB = imageBytes.length / 1024;
                if (fileSizeInKB > 2048) {
                    Toast.makeText(this, "Ảnh phải nhỏ hơn 2MB", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo MultipartBody.Part để gửi ảnh
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                avatarPart = MultipartBody.Part.createFormData("avatar", "profile_image.jpg", requestFile);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Call<Void> call = jobApi.saveProfile(userIdBody, avatarPart, fullNameBody, jobTitle, emailBody, phone, skills, experience, preference, location, salary, education, industry);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CompleteProfileActivity.this, "Profile đã được lưu", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CompleteProfileActivity.this, MainActivity.class));
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có chi tiết lỗi";
                        Toast.makeText(CompleteProfileActivity.this, "Lưu thất bại - Mã: " + response.code() + " - Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                        Log.e("CompleteProfile", "Error: " + errorBody);
                    } catch (IOException e) {
                        Toast.makeText(CompleteProfileActivity.this, "Lỗi đọc phản hồi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CompleteProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getLoggedInUserId() {
        int userId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("user_id", -1);
        Toast.makeText(this, "User ID from SharedPreferences: " + userId, Toast.LENGTH_LONG).show();
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            finish();
        }
        return userId;
    }

    // Phương thức getRealPathFromURI không còn cần thiết nữa, nhưng tôi để lại để tham khảo
    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }

    public interface JobApi {
        @Multipart
        @POST("api/profile/save")
        Call<Void> saveProfile(
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
    }
}