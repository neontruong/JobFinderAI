package com.example.jobfinder01.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobfinder01.Models.Job;
import com.example.jobfinder01.R;
import com.example.jobfinder01.ui.home.JobAdapter;
import com.example.jobfinder01.ui.home.JobDetailActivity;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SavedJobsActivity extends AppCompatActivity implements JobAdapter.OnSaveClickListener, JobAdapter.OnJobClickListener {
    private JobAdapter savedJobAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_jobs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerSavedJobs);
        refreshSavedJobsList();
    }

    private void refreshSavedJobsList() {
        List<Job> savedJobs = getSavedJobs();
        savedJobAdapter = new JobAdapter(this, savedJobs);
        savedJobAdapter.setOnSaveClickListener(this);
        savedJobAdapter.setOnJobClickListener(this);
        recyclerView.setAdapter(savedJobAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<Job> getSavedJobs() {
        SharedPreferences userPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            return new ArrayList<>(); // Trả về danh sách rỗng nếu không có userId
        }

        List<Job> savedJobs = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("SavedJobs_" + userId, MODE_PRIVATE);
        Gson gson = new Gson();
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith("job_")) {
                String jobJson = (String) entry.getValue();
                Job job = gson.fromJson(jobJson, Job.class);
                savedJobs.add(job);
            }
        }
        return savedJobs;
    }

    @Override
    public void onSaveClick(Job job, ImageButton saveButton) {
        SharedPreferences userPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để lưu công việc!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("SavedJobs_" + userId, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        String jobJson = prefs.getString("job_" + job.getId(), null);
        if (jobJson == null) {
            jobJson = gson.toJson(job);
            editor.putString("job_" + job.getId(), jobJson);
            editor.apply();
            saveButton.setImageResource(R.drawable.ic_bookmark_filled);
            Toast.makeText(this, "Đã lưu " + job.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            editor.remove("job_" + job.getId());
            editor.apply();
            saveButton.setImageResource(R.drawable.ic_bookmark_outline);
            Toast.makeText(this, "Đã bỏ lưu " + job.getTitle(), Toast.LENGTH_SHORT).show();
        }

        refreshSavedJobsList();
    }

    @Override
    public void onJobClick(Job job) {
        if (job.getId() == 0) {
            Toast.makeText(this, "Không tìm thấy ID công việc!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, JobDetailActivity.class);
        intent.putExtra("job_id", job.getId());
        startActivity(intent);
    }
}