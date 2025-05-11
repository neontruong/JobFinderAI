package com.example.jobfinder01.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.jobfinder01.API.ApiClient;
import com.example.jobfinder01.API.ApiService;
import com.example.jobfinder01.Models.Job;
import com.example.jobfinder01.Models.UserProfile;
import com.example.jobfinder01.R;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements JobAdapter.OnSaveClickListener {

    private RecyclerView recyclerJobList;
    private JobAdapter jobAdapter;
    private List<Job> jobList;
    private List<Job> filteredJobList;
    private Spinner spnJobFilter;
    private MaterialButton btnSearchJob;
    private EditText edtSearchJob;
    private ApiService apiService;
    private ImageView imgProfile;
    private TextView tvUserName;
    private ViewPager2 viewPagerBanner;
    private LinearLayout dotsContainer;
    private Handler bannerHandler;
    private Runnable bannerRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerJobList = view.findViewById(R.id.recyclerJobList);
        spnJobFilter = view.findViewById(R.id.spnJobFilter);
        btnSearchJob = view.findViewById(R.id.btnSearchJob);
        edtSearchJob = view.findViewById(R.id.edtSearchJob);
        imgProfile = view.findViewById(R.id.imgProfile);
        tvUserName = view.findViewById(R.id.tvUserName);
        viewPagerBanner = view.findViewById(R.id.viewPagerBanner);
        dotsContainer = view.findViewById(R.id.dotsContainer);

        if (recyclerJobList == null) {
            Log.e("HomeFragment", "recyclerJobList is null. Check XML layout!");
            return view;
        }
        if (spnJobFilter == null) {
            Log.e("HomeFragment", "spnJobFilter is null. Check XML layout!");
            return view;
        }
        if (dotsContainer == null) {
            Log.e("HomeFragment", "dotsContainer is null. Check XML layout!");
            return view;
        }

        setupRecyclerView();
        setupJobFilter();
        setupRetrofit();
        setupBanner();

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            loadUserProfile(userId);
        } else {
            Toast.makeText(getContext(), "Không tìm thấy user ID. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            tvUserName.setText("Welcome, Guest");
        }

        if (btnSearchJob != null) {
            btnSearchJob.setOnClickListener(v -> filterJobs());
        } else {
            Log.e("HomeFragment", "btnSearchJob is null");
        }

        loadJobs();

        return view;
    }

    private void loadUserProfile(int userId) {
        ApiService apiService = ApiClient.getAuthClient(requireContext()).create(ApiService.class);
        Call<UserProfile> call = apiService.getProfile(userId);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile userProfile = response.body();
                    tvUserName.setText("Welcome, " + userProfile.getFullName());

                    String avatarUrl = userProfile.getAvatar();
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(HomeFragment.this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .into(imgProfile);
                    } else {
                        imgProfile.setImageResource(R.drawable.ic_profile);
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi tải thông tin profile: " + response.code(), Toast.LENGTH_SHORT).show();
                    tvUserName.setText("Welcome, Guest");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                tvUserName.setText("Welcome, Guest");
            }
        });
    }

    private void setupRecyclerView() {
        if (recyclerJobList == null) {
            Log.e("HomeFragment", "recyclerJobList is null in setupRecyclerView");
            return;
        }
        jobList = new ArrayList<>();
        filteredJobList = new ArrayList<>();
        jobAdapter = new JobAdapter(getContext(), filteredJobList);

        recyclerJobList.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerJobList.setAdapter(jobAdapter);

        jobAdapter.setOnJobClickListener(job -> {
            if (job.getId() == 0) {
                Toast.makeText(getContext(), "Không tìm thấy ID công việc!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getContext(), JobDetailActivity.class);
            intent.putExtra("job_id", job.getId());
            startActivity(intent);
        });

        jobAdapter.setOnSaveClickListener(this);
    }

    @Override
    public void onSaveClick(Job job, ImageButton saveButton) {
        SharedPreferences userPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để lưu công việc!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("SavedJobs_" + userId, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        String jobJson = prefs.getString("job_" + job.getId(), null);
        if (jobJson == null) {
            jobJson = gson.toJson(job);
            editor.putString("job_" + job.getId(), jobJson);
            editor.apply();
            saveButton.setImageResource(R.drawable.ic_bookmark_filled);
            Toast.makeText(getContext(), "Đã lưu " + job.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            editor.remove("job_" + job.getId());
            editor.apply();
            saveButton.setImageResource(R.drawable.ic_bookmark_outline);
            Toast.makeText(getContext(), "Đã bỏ lưu " + job.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupJobFilter() {
        if (spnJobFilter == null) {
            Log.e("HomeFragment", "spnJobFilter is null in setupJobFilter");
            return;
        }
        String[] filters = {"All", "Remote", "Full-Time", "Part-Time", "Internship"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filters);
        spnJobFilter.setAdapter(adapter);
    }

    private void setupRetrofit() {
        if (getContext() == null) {
            Log.e("HomeFragment", "Context is null in setupRetrofit");
            return;
        }
        try {
            apiService = ApiClient.getAuthClient(getContext()).create(ApiService.class);
        } catch (Exception e) {
            Log.e("HomeFragment", "Error setting up Retrofit: " + e.getMessage());
        }
    }

    private void loadJobs() {
        if (apiService == null) {
            Log.e("HomeFragment", "apiService is null in loadJobs");
            Toast.makeText(getContext(), "Lỗi kết nối API!", Toast.LENGTH_SHORT).show();
            return;
        }
        apiService.getAllJobs().enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    jobList.clear();
                    filteredJobList.clear();
                    jobList.addAll(response.body());
                    filteredJobList.addAll(jobList);
                    jobAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Lỗi phản hồi từ API: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Lỗi khi tải công việc: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterJobs() {
        if (edtSearchJob == null || spnJobFilter == null) {
            Log.e("HomeFragment", "edtSearchJob or spnJobFilter is null in filterJobs");
            return;
        }
        String keyword = edtSearchJob.getText().toString().trim().toLowerCase(Locale.ROOT);
        String selectedFilter = spnJobFilter.getSelectedItem().toString();

        filteredJobList.clear();

        for (Job job : jobList) {
            boolean matchesKeyword = TextUtils.isEmpty(keyword) ||
                    job.getTitle().toLowerCase(Locale.ROOT).contains(keyword) ||
                    job.getCompany().toLowerCase(Locale.ROOT).contains(keyword) ||
                    job.getLocation().toLowerCase(Locale.ROOT).contains(keyword);

            boolean matchesFilter = selectedFilter.equals("All") ||
                    (selectedFilter.equals("Remote") && job.getLocation().toLowerCase(Locale.ROOT).contains("từ xa")) ||
                    job.getTitle().toLowerCase(Locale.ROOT).contains(selectedFilter.toLowerCase(Locale.ROOT));

            if (matchesKeyword && matchesFilter) {
                filteredJobList.add(job);
            }
        }

        if (filteredJobList.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy công việc!", Toast.LENGTH_SHORT).show();
        }

        jobAdapter.notifyDataSetChanged();
    }

    private void setupBanner() {
        if (viewPagerBanner == null || dotsContainer == null) {
            Log.e("HomeFragment", "viewPagerBanner or dotsContainer is null in setupBanner");
            return;
        }
        List<Integer> bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner1);
        bannerImages.add(R.drawable.banner2);
        bannerImages.add(R.drawable.banner3);

        BannerAdapter bannerAdapter = new BannerAdapter(bannerImages);
        viewPagerBanner.setAdapter(bannerAdapter);

        setupDotsIndicator(bannerImages.size());
        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDotsIndicator(position);
            }
        });

        bannerHandler = new Handler(Looper.getMainLooper());
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerImages.size() == 0) return;
                int currentItem = viewPagerBanner.getCurrentItem();
                int nextItem = (currentItem + 1) % bannerImages.size();
                viewPagerBanner.setCurrentItem(nextItem, true);
                bannerHandler.postDelayed(this, 15000);
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 15000);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.postDelayed(bannerRunnable, 15000);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }

    private void setupDotsIndicator(int count) {
        if (dotsContainer == null) {
            Log.e("HomeFragment", "dotsContainer is null in setupDotsIndicator");
            return;
        }
        dotsContainer.removeAllViews();
        for (int i = 0; i < count; i++) {
            TextView dot = new TextView(getContext());
            dot.setText("•");
            dot.setTextSize(24);
            dot.setTextColor(getResources().getColor(android.R.color.darker_gray));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dotsContainer.addView(dot);
        }
        if (count > 0) {
            ((TextView) dotsContainer.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void updateDotsIndicator(int position) {
        if (dotsContainer == null) {
            Log.e("HomeFragment", "dotsContainer is null in updateDotsIndicator");
            return;
        }
        for (int i = 0; i < dotsContainer.getChildCount(); i++) {
            TextView dot = (TextView) dotsContainer.getChildAt(i);
            dot.setTextColor(getResources().getColor(i == position ? android.R.color.black : android.R.color.darker_gray));
        }
    }

    private static class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
        private final List<Integer> bannerImages;

        BannerAdapter(List<Integer> bannerImages) {
            this.bannerImages = bannerImages;
        }

        @NonNull
        @Override
        public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
            return new BannerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
            holder.imageView.setImageResource(bannerImages.get(position));
        }

        @Override
        public int getItemCount() {
            return bannerImages.size();
        }

        static class BannerViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            BannerViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.bannerImage);
            }
        }
    }
}