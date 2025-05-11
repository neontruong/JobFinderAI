package com.example.jobfinder01.ui.jobapplications;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.jobfinder01.API.ApiClient;
import com.example.jobfinder01.API.ApiService;
import com.example.jobfinder01.R;
import com.example.jobfinder01.ui.home.JobDetailActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobApplicationsFragment extends Fragment {

    private RecyclerView rvJobApplications;
    private TextView tvNoApplications;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TabLayout tabLayout;
    private PieChart pieChart;
    private JobApplicationAdapter adapter;
    private List<JobApplication> applicationList;
    private List<JobApplication> filteredList;
    private ApiService apiService;
    private int currentTabPosition = 0; // Biến để lưu tab hiện tại

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Cho phép fragment có menu
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_applications, container, false);

        // Ánh xạ view
        rvJobApplications = view.findViewById(R.id.rvJobApplications);
        tvNoApplications = view.findViewById(R.id.tvNoApplications);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tabLayout = view.findViewById(R.id.tabLayout);
        pieChart = view.findViewById(R.id.pieChart);

        // Khởi tạo ApiService
        apiService = ApiClient.getAuthClient(getContext()).create(ApiService.class);

        // Khởi tạo danh sách
        applicationList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Thiết lập RecyclerView
        adapter = new JobApplicationAdapter(filteredList);
        rvJobApplications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvJobApplications.setAdapter(adapter);

        // Tải dữ liệu từ API
        loadApplications();

        // Xử lý làm mới
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadApplications();
        });

        // Xử lý lọc theo trạng thái
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                filterApplications(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void loadApplications() {
        // Lấy user_id từ SharedPreferences
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String token = prefs.getString("auth_token", null);

        if (userId == -1 || token == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem đơn ứng tuyển!", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        // Hiển thị trạng thái tải
        swipeRefreshLayout.setRefreshing(true);

        // Gọi API
        apiService.getUserApplications().enqueue(new Callback<List<JobApplication>>() {
            @Override
            public void onResponse(Call<List<JobApplication>> call, Response<List<JobApplication>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("JobApplicationsFragment", "Response: " + response.body().toString());
                    applicationList.clear();
                    applicationList.addAll(response.body());
                    filterApplications(currentTabPosition); // Lọc lại theo tab hiện tại
                    setupPieChart();
                    Snackbar.make(getView(), "Danh sách đã được làm mới!", Snackbar.LENGTH_SHORT).show();
                } else {
                    String errorMessage = "Lỗi tải danh sách đơn: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            errorMessage += " - Không thể đọc thông điệp lỗi";
                        }
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<JobApplication>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUI() {
        adapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            rvJobApplications.setVisibility(View.GONE);
            tvNoApplications.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Không có đơn ứng tuyển nào trong trạng thái này!", Toast.LENGTH_SHORT).show();
        } else {
            rvJobApplications.setVisibility(View.VISIBLE);
            tvNoApplications.setVisibility(View.GONE);
        }
    }

    private void setupPieChart() {
        int pending = 0, approved = 0, rejected = 0;
        for (JobApplication app : applicationList) {
            switch (app.getStatus()) {
                case "pending":
                    pending++;
                    break;
                case "approved":
                    approved++;
                    break;
                case "rejected":
                    rejected++;
                    break;
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        if (pending > 0) entries.add(new PieEntry(pending, "Đang chờ"));
        if (approved > 0) entries.add(new PieEntry(approved, "Đã duyệt"));
        if (rejected > 0) entries.add(new PieEntry(rejected, "Bị từ chối"));

        PieDataSet dataSet = new PieDataSet(entries, "Trạng thái đơn");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(android.R.color.white);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Thống kê đơn");
        pieChart.setCenterTextSize(14f);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void filterApplications(int tabPosition) {
        filteredList.clear();
        switch (tabPosition) {
            case 0: // Tất cả
                filteredList.addAll(applicationList);
                break;
            case 1: // Đang chờ
                for (JobApplication app : applicationList) {
                    if (app.getStatus().equals("pending")) {
                        filteredList.add(app);
                    }
                }
                break;
            case 2: // Đã duyệt
                for (JobApplication app : applicationList) {
                    if (app.getStatus().equals("approved")) {
                        filteredList.add(app);
                    }
                }
                break;
            case 3: // Bị từ chối
                for (JobApplication app : applicationList) {
                    if (app.getStatus().equals("rejected")) {
                        filteredList.add(app);
                    }
                }
                break;
        }
        adapter.notifyDataSetChanged();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_job_applications, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Tìm kiếm công việc...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBySearch(newText);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            sortApplications();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterBySearch(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(applicationList);
        } else {
            for (JobApplication app : applicationList) {
                if (app.getJobTitle().toLowerCase().contains(query.toLowerCase()) ||
                        app.getCompany().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(app);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateUI();
    }

    private void sortApplications() {
        Collections.sort(applicationList, (app1, app2) -> app2.getAppliedDate().compareTo(app1.getAppliedDate()));
        filterApplications(currentTabPosition);
        Toast.makeText(getContext(), "Đã sắp xếp theo ngày ứng tuyển (mới nhất)", Toast.LENGTH_SHORT).show();
    }
}