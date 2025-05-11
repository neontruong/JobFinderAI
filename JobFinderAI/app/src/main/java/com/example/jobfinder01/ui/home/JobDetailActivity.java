package com.example.jobfinder01.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.jobfinder01.API.ApiClient;
import com.example.jobfinder01.API.ApiService;
import com.example.jobfinder01.CV.CV;
import com.example.jobfinder01.CV.CVResponse;
import com.example.jobfinder01.CV.CreateCVActivity;
import com.example.jobfinder01.LoginActivity;
import com.example.jobfinder01.Models.Job;
import com.example.jobfinder01.R;
import com.example.jobfinder01.ui.home.apply.ApplicationRequest;
import com.example.jobfinder01.ui.notifications.Notification;
import com.example.jobfinder01.Zalopay.AppInfo;
import com.example.jobfinder01.Zalopay.CreateOrder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class JobDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvCompany, tvCompanyDescription, tvLocation, tvSalary, tvDescription, tvContactPhone, tvContactEmail, tvPostedAt;
    private ImageView ivCompanyLogo, ivCall, ivZalo;
    private Button btnApply;
    private ApiService apiService;
    private int jobId;
    private String contactPhone;
    private Job currentJob;

    private static final int MAX_FREE_APPLICATIONS = 3;
    private static final int EXTRA_APPLICATIONS_PER_PAYMENT = 2;
    private static final String PREF_APPLICATION_COUNT = "application_count";
    private static final String PREF_REMAINING_FREE_APPLICATIONS = "remaining_free_applications";
    private static final int MAX_RECENT_JOBS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_job_detail);

        try {
            ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);
            Log.d("JobDetailActivity", "ZaloPay SDK initialized successfully");
        } catch (Exception e) {
            Log.e("JobDetailActivity", "Error initializing ZaloPay SDK: " + e.getMessage());
            Toast.makeText(this, "Lỗi khởi tạo thanh toán!", Toast.LENGTH_SHORT).show();
        }

        tvTitle = findViewById(R.id.tvJobTitle);
        tvCompany = findViewById(R.id.tvCompany);
        tvCompanyDescription = findViewById(R.id.tvCompanyDescription);
        tvLocation = findViewById(R.id.tvLocation);
        tvSalary = findViewById(R.id.tvSalary);
        tvDescription = findViewById(R.id.tvDescription);
        tvContactPhone = findViewById(R.id.tvContactPhone);
        tvContactEmail = findViewById(R.id.tvContactEmail);
        tvPostedAt = findViewById(R.id.tvPostedAt);
        ivCompanyLogo = findViewById(R.id.ivCompanyLogo);
        btnApply = findViewById(R.id.btnApply);
        ivCall = findViewById(R.id.ivCall);
        ivZalo = findViewById(R.id.ivZalo);

        if (btnApply == null || ivCall == null || ivZalo == null) {
            Log.e("JobDetailActivity", "One or more views are null - Check your layout file!");
            Toast.makeText(this, "Lỗi giao diện!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getAuthClient(this).create(ApiService.class);

        jobId = getIntent().getIntExtra("job_id", -1);
        if (jobId != -1) {
            loadJobDetails(jobId);
        } else {
            Toast.makeText(this, "Không tìm thấy công việc!", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnApply.setOnClickListener(v -> checkCVsAndApply());
        ivCall.setOnClickListener(v -> startCalling());
        ivZalo.setOnClickListener(v -> startMessaging());
    }

    private void loadJobDetails(int jobId) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối internet!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService.getJobById(jobId).enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentJob = response.body();

                    tvTitle.setText(currentJob.getTitle() != null ? currentJob.getTitle() : "Không có thông tin");
                    tvCompany.setText(currentJob.getCompany() != null ? currentJob.getCompany() : "Không có thông tin");
                    tvCompanyDescription.setText(currentJob.getCompanyDescription() != null ? currentJob.getCompanyDescription() : "Không có thông tin");
                    tvLocation.setText(currentJob.getLocation() != null ? currentJob.getLocation() : "Không có thông tin");

                    Double salary = currentJob.getSalary();
                    if (salary != null) {
                        tvSalary.setText(String.format("$%.2f", salary));
                    } else {
                        tvSalary.setText("Không có thông tin");
                    }

                    tvDescription.setText(currentJob.getDescription() != null ? currentJob.getDescription() : "Không có thông tin");
                    tvContactPhone.setText(currentJob.getContactPhone() != null ? currentJob.getContactPhone() : "Không có thông tin");
                    tvContactEmail.setText(currentJob.getContactEmail() != null ? currentJob.getContactEmail() : "Không có thông tin");

                    contactPhone = currentJob.getContactPhone();

                    String postedAt = currentJob.getPostedAt();
                    if (postedAt != null) {
                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            Date date = inputFormat.parse(postedAt);
                            tvPostedAt.setText(date != null ? outputFormat.format(date) : postedAt);
                        } catch (ParseException e) {
                            tvPostedAt.setText(postedAt);
                            Log.e("JobDetailActivity", "Lỗi khi parse ngày: " + e.getMessage());
                        }
                    } else {
                        tvPostedAt.setText("Không có thông tin");
                    }

                    String logoUrl = currentJob.getLogoUrl();
                    Glide.with(JobDetailActivity.this)
                            .load(logoUrl != null ? logoUrl : "")
                            .placeholder(R.drawable.placeholder_logoo)
                            .error(R.drawable.default_logo)
                            .into(ivCompanyLogo);

                    saveToRecentJobs(currentJob);
                } else {
                    Toast.makeText(JobDetailActivity.this, "Lỗi tải chi tiết công việc! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                Toast.makeText(JobDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("JobDetailActivity", "Gọi API thất bại: " + t.getMessage(), t);
                finish();
            }
        });
    }

    private void saveToRecentJobs(Job job) {
        SharedPreferences userPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            return; // Không lưu nếu không có userId
        }

        SharedPreferences prefs = getSharedPreferences("RecentJobs_" + userId, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        List<Job> recentJobs = new ArrayList<>();
        for (int i = 0; i < MAX_RECENT_JOBS; i++) {
            String jobJson = prefs.getString("recent_job_" + i, null);
            if (jobJson != null) {
                Job recentJob = gson.fromJson(jobJson, Job.class);
                if (recentJob.getId() != job.getId()) {
                    recentJobs.add(recentJob);
                }
            }
        }

        recentJobs.add(0, job);

        if (recentJobs.size() > MAX_RECENT_JOBS) {
            recentJobs = recentJobs.subList(0, MAX_RECENT_JOBS);
        }

        for (int i = 0; i < recentJobs.size(); i++) {
            String jobJson = gson.toJson(recentJobs.get(i));
            editor.putString("recent_job_" + i, jobJson);
        }

        for (int i = recentJobs.size(); i < MAX_RECENT_JOBS; i++) {
            editor.remove("recent_job_" + i);
        }

        editor.apply();
    }

    private void startCalling() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để gọi điện!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối internet!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contactPhone == null || contactPhone.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy số điện thoại của nhà tuyển dụng!", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = contactPhone.replaceAll("[^0-9]", "");
        if (phoneNumber.startsWith("84")) {
            phoneNumber = "0" + phoneNumber.substring(2);
        } else if (!phoneNumber.startsWith("0")) {
            phoneNumber = "0" + phoneNumber;
        }

        if (phoneNumber.length() != 10) {
            Toast.makeText(this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở ứng dụng gọi điện!", Toast.LENGTH_SHORT).show();
            Log.e("JobDetailActivity", "Lỗi khi mở ứng dụng gọi điện: " + e.getMessage());
        }
    }

    private void startMessaging() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để nhắn tin!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối internet!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contactPhone == null || contactPhone.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy số điện thoại của nhà tuyển dụng!", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = contactPhone.replaceAll("[^0-9]", "");
        if (phoneNumber.startsWith("84")) {
            phoneNumber = "0" + phoneNumber.substring(2);
        } else if (!phoneNumber.startsWith("0")) {
            phoneNumber = "0" + phoneNumber;
        }

        if (phoneNumber.length() != 10) {
            Toast.makeText(this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isZaloInstalled()) {
            Toast.makeText(this, "Vui lòng cài đặt Zalo để nhắn tin!", Toast.LENGTH_SHORT).show();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.zing.zalo")));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.zing.zalo")));
            }
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://zalo.me/" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở Zalo!", Toast.LENGTH_SHORT).show();
            Log.e("JobDetailActivity", "Lỗi khi mở Zalo: " + e.getMessage());
        }
    }

    private boolean isZaloInstalled() {
        try {
            getPackageManager().getPackageInfo("com.zing.zalo", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void checkCVsAndApply() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để ứng tuyển!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        int applicationCount = prefs.getInt(PREF_APPLICATION_COUNT + "_" + userId, 0);
        int remainingFreeApplications = prefs.getInt(PREF_REMAINING_FREE_APPLICATIONS + "_" + userId, 0);

        Log.d("JobDetailActivity", "Application count: " + applicationCount + ", Remaining free applications: " + remainingFreeApplications);

        if (applicationCount < MAX_FREE_APPLICATIONS || remainingFreeApplications > 0) {
            checkCVsFromServer();
        } else {
            showPaymentDialog();
        }
    }

    private void checkCVsFromServer() {
        Call<CVResponse> call = apiService.getCVs();
        call.enqueue(new Callback<CVResponse>() {
            @Override
            public void onResponse(Call<CVResponse> call, Response<CVResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CV> cvList = response.body().getData();
                    if (cvList == null || cvList.isEmpty()) {
                        Toast.makeText(JobDetailActivity.this, "Bạn chưa có CV. Vui lòng tạo CV trước!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(JobDetailActivity.this, CreateCVActivity.class);
                        startActivity(intent);
                    } else {
                        showCVSelectionDialog(cvList);
                    }
                } else {
                    Toast.makeText(JobDetailActivity.this, "Lỗi khi kiểm tra CV: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CVResponse> call, Throwable t) {
                Toast.makeText(JobDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCVSelectionDialog(List<CV> cvList) {
        String[] cvTitles = cvList.stream().map(CV::getJobTitle).toArray(String[]::new);
        new MaterialAlertDialogBuilder(this)
                .setTitle("Chọn CV để ứng tuyển")
                .setItems(cvTitles, (dialog, which) -> {
                    CV selectedCV = cvList.get(which);
                    applyForJob(selectedCV.getId());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void applyForJob(int cvId) {
        Log.d("JobDetailActivity", "Applying with jobId: " + jobId + ", cvId: " + cvId);
        ApplicationRequest request = new ApplicationRequest(jobId, cvId);
        Call<Void> call = apiService.applyForJob(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(JobDetailActivity.this, "Ứng tuyển thành công!", Toast.LENGTH_SHORT).show();
                    sendNotificationToUser();
                    incrementApplicationCount();
                } else {
                    if (response.code() == 400) {
                        Toast.makeText(JobDetailActivity.this, "Bạn đã ứng tuyển công việc này!", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 422) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("JobDetailActivity", "Error 422: " + errorBody);
                            Toast.makeText(JobDetailActivity.this, "Dữ liệu không hợp lệ: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(JobDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void incrementApplicationCount() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        int applicationCount = prefs.getInt(PREF_APPLICATION_COUNT + "_" + userId, 0);
        int remainingFreeApplications = prefs.getInt(PREF_REMAINING_FREE_APPLICATIONS + "_" + userId, 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_APPLICATION_COUNT + "_" + userId, applicationCount + 1);

        if (applicationCount >= MAX_FREE_APPLICATIONS && remainingFreeApplications > 0) {
            editor.putInt(PREF_REMAINING_FREE_APPLICATIONS + "_" + userId, remainingFreeApplications - 1);
        }

        editor.apply();
        Log.d("JobDetailActivity", "Incremented application count to: " + (applicationCount + 1) +
                ", Remaining free applications: " + (remainingFreeApplications > 0 ? remainingFreeApplications - 1 : 0));
    }

    private void showPaymentDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Yêu cầu thanh toán")
                .setMessage("Bạn đã hết lượt miễn phí. Thanh toán 50,000 VND để nhận thêm " + EXTRA_APPLICATIONS_PER_PAYMENT + " lượt ứng tuyển.")
                .setPositiveButton("Thanh toán qua ZaloPay", (dialog, which) -> initiateZaloPayPayment())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void initiateZaloPayPayment() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối internet!", Toast.LENGTH_SHORT).show();
            Log.e("JobDetailActivity", "No internet connection");
            return;
        }

        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... voids) {
                CreateOrder orderApi = new CreateOrder();
                try {
                    Log.d("JobDetailActivity", "Step 1: Creating ZaloPay order in background thread...");
                    JSONObject data = orderApi.createOrder("50000");
                    return data;
                } catch (Exception e) {
                    Log.e("JobDetailActivity", "Error in background thread: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject data) {
                Log.d("JobDetailActivity", "Step 2: CreateOrder result: " + (data != null ? data.toString() : "null"));

                if (data == null) {
                    Toast.makeText(JobDetailActivity.this, "Lỗi tạo đơn hàng: Dữ liệu trả về null!", Toast.LENGTH_SHORT).show();
                    Log.e("JobDetailActivity", "CreateOrder returned null");
                    return;
                }

                Log.d("JobDetailActivity", "Step 3: Extracting return_code...");
                String code = data.optString("return_code", "0");
                Log.d("JobDetailActivity", "Step 4: ZaloPay return_code: " + code);

                if (code.equals("1")) {
                    Log.d("JobDetailActivity", "Step 5: Extracting zp_trans_token...");
                    String zpTransToken = data.optString("zp_trans_token", "");
                    Log.d("JobDetailActivity", "Step 6: zp_trans_token: " + zpTransToken);

                    if (zpTransToken.isEmpty()) {
                        Toast.makeText(JobDetailActivity.this, "Lỗi: Token giao dịch không hợp lệ!", Toast.LENGTH_SHORT).show();
                        Log.e("JobDetailActivity", "zp_trans_token is empty");
                        return;
                    }

                    Log.d("JobDetailActivity", "Step 7: Checking ZaloPaySDK instance...");
                    if (ZaloPaySDK.getInstance() == null) {
                        Toast.makeText(JobDetailActivity.this, "Lỗi: ZaloPay SDK chưa được khởi tạo!", Toast.LENGTH_SHORT).show();
                        Log.e("JobDetailActivity", "ZaloPaySDK instance is null");
                        return;
                    }

                    Log.d("JobDetailActivity", "Step 8: Initiating ZaloPay payment...");
                    ZaloPaySDK.getInstance().payOrder(JobDetailActivity.this, zpTransToken, "demozpdk://app", new PayOrderListener() {
                        @Override
                        public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                            Log.d("JobDetailActivity", "Payment succeeded: TransactionID=" + transactionId);
                            runOnUiThread(() -> {
                                new MaterialAlertDialogBuilder(JobDetailActivity.this)
                                        .setTitle("Thanh toán thành công")
                                        .setMessage("Bạn đã nhận thêm " + EXTRA_APPLICATIONS_PER_PAYMENT + " lượt ứng tuyển miễn phí!")
                                        .setPositiveButton("OK", (dialog, which) -> resetApplicationCountAfterPayment())
                                        .show();
                            });
                        }

                        @Override
                        public void onPaymentCanceled(String zpTransToken, String appTransID) {
                            Toast.makeText(JobDetailActivity.this, "Bạn đã hủy thanh toán!", Toast.LENGTH_SHORT).show();
                            Log.d("JobDetailActivity", "Payment canceled: zpTransToken=" + zpTransToken);
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                            Toast.makeText(JobDetailActivity.this, "Thanh toán thất bại: " + zaloPayError, Toast.LENGTH_SHORT).show();
                            Log.e("JobDetailActivity", "Payment error: " + zaloPayError.toString());
                        }
                    });
                } else {
                    String errorMessage = data.optString("return_message", "Unknown error");
                    Toast.makeText(JobDetailActivity.this, "Lỗi tạo đơn hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("JobDetailActivity", "ZaloPay error: " + errorMessage);
                }
            }
        }.execute();
    }

    private void resetApplicationCountAfterPayment() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        SharedPreferences.Editor editor = prefs.edit();

        int currentRemaining = prefs.getInt(PREF_REMAINING_FREE_APPLICATIONS + "_" + userId, 0);
        editor.putInt(PREF_REMAINING_FREE_APPLICATIONS + "_" + userId, currentRemaining + EXTRA_APPLICATIONS_PER_PAYMENT);
        editor.apply();

        Log.d("JobDetailActivity", "Payment completed, added " + EXTRA_APPLICATIONS_PER_PAYMENT +
                " free applications. New remaining: " + (currentRemaining + EXTRA_APPLICATIONS_PER_PAYMENT));

        checkCVsFromServer();
    }

    private void sendNotificationToUser() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String notificationMessage = "Bạn đã ứng tuyển thành công công việc: " + (tvTitle != null ? tvTitle.getText() : "Unknown");
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        String notificationsJson = prefs.getString("notifications", "[]");
        List<Notification> notifications = new ArrayList<>();
        try {
            notifications = new Gson().fromJson(notificationsJson, new TypeToken<List<Notification>>() {}.getType());
            Log.d("JobDetailActivity", "Current notifications: " + notificationsJson);
        } catch (Exception e) {
            Log.e("JobDetailActivity", "Error parsing notifications: " + e.getMessage());
            e.printStackTrace();
        }

        notifications.add(new Notification(notificationMessage, timestamp));
        Log.d("JobDetailActivity", "Adding new notification: " + notificationMessage);

        SharedPreferences.Editor editor = prefs.edit();
        String newNotificationsJson = new Gson().toJson(notifications);
        editor.putString("notifications", newNotificationsJson);
        editor.apply();
        Log.d("JobDetailActivity", "Saved notifications: " + newNotificationsJson);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ZaloPaySDK.getInstance() != null) {
            ZaloPaySDK.getInstance().onResult(intent);
        } else {
            Log.e("JobDetailActivity", "ZaloPaySDK instance is null in onNewIntent");
        }
    }
}