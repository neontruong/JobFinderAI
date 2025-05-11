package com.example.jobfinder01.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.jobfinder01.Models.Job;
import com.example.jobfinder01.R;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private final List<Job> jobList;
    private final Context context;
    private OnJobClickListener onJobClickListener;
    private OnSaveClickListener onSaveClickListener;

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    public interface OnSaveClickListener {
        void onSaveClick(Job job, ImageButton saveButton);
    }

    public JobAdapter(Context context, List<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    public void setOnJobClickListener(OnJobClickListener listener) {
        this.onJobClickListener = listener;
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.onSaveClickListener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.tvTitle.setText(job.getTitle());
        holder.tvCompany.setText(job.getCompany());
        holder.tvLocation.setText(job.getLocation());
        holder.tvSalary.setText(String.format("%,.0f $", job.getSalary()));

        if (job.getLogoUrl() != null && !job.getLogoUrl().isEmpty()) {
            holder.logoImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(job.getLogoUrl())
                    .placeholder(R.drawable.placeholder_logoo)
                    .error(R.drawable.default_logo)
                    .into(holder.logoImageView);
        } else {
            holder.logoImageView.setVisibility(View.GONE);
        }

        SharedPreferences userPrefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userId = userPrefs.getInt("user_id", -1);
        boolean isSaved = false;
        if (userId != -1) {
            SharedPreferences prefs = context.getSharedPreferences("SavedJobs_" + userId, Context.MODE_PRIVATE);
            isSaved = prefs.getString("job_" + job.getId(), null) != null;
        }
        holder.btnSaveJob.setImageResource(isSaved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);

        holder.btnSaveJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSaveClickListener != null) {
                    onSaveClickListener.onSaveClick(job, holder.btnSaveJob);
                }
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onJobClickListener != null) {
                onJobClickListener.onJobClick(job);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCompany, tvLocation, tvSalary;
        ImageView logoImageView;
        ImageButton btnSaveJob;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            logoImageView = itemView.findViewById(R.id.logoImageView);
            btnSaveJob = itemView.findViewById(R.id.btnSaveJob);
        }
    }
}