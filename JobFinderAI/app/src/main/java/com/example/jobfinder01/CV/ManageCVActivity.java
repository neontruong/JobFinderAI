package com.example.jobfinder01.CV;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobfinder01.API.ApiClient;
import com.example.jobfinder01.API.ApiService;
import com.example.jobfinder01.R;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageCVActivity extends AppCompatActivity {
    private RecyclerView rvCVs;
    private TextView tvEmpty;
    private Button btnCreateCV;
    private CVAdapter cvAdapter;
    private List<CV> cvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cv);

        rvCVs = findViewById(R.id.rvCVs);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnCreateCV = findViewById(R.id.btnCreateCV);

        cvList = new ArrayList<>();
        cvAdapter = new CVAdapter(this, cvList);
        rvCVs.setLayoutManager(new LinearLayoutManager(this));
        rvCVs.setAdapter(cvAdapter);

        btnCreateCV.setOnClickListener(v -> {
            Intent intent = new Intent(ManageCVActivity.this, CreateCVActivity.class);
            startActivity(intent);
        });

        loadCVs();
    }

    private void loadCVs() {
        ApiService apiService = ApiClient.getAuthClient(this).create(ApiService.class);
        Call<CVResponse> call = apiService.getCVs();
        call.enqueue(new Callback<CVResponse>() {
            @Override
            public void onResponse(Call<CVResponse> call, Response<CVResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cvList.clear();
                    List<CV> fetchedCVs = response.body().getData();
                    if (fetchedCVs != null) {
                        cvList.addAll(fetchedCVs);
                    }
                    cvAdapter.notifyDataSetChanged();

                    // Log danh sách CV để kiểm tra
                    for (CV cv : cvList) {
                        Log.d("ManageCVActivity", "CV ID: " + cv.getId() + ", Full Name: " + cv.getFullName());
                    }

                    // Hiển thị hoặc ẩn tvEmpty
                    if (cvList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvCVs.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rvCVs.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(ManageCVActivity.this, "Lỗi khi lấy danh sách CV: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CVResponse> call, Throwable t) {
                Toast.makeText(ManageCVActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCVs(); // Làm mới danh sách CV mỗi khi activity được quay lại
    }
}