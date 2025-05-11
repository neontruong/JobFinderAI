package com.example.jobfinder01.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder01.Models.Job; // Sử dụng Models.Job
import com.example.jobfinder01.R;
import com.example.jobfinder01.ui.home.JobDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class JobAdapterForAI extends RecyclerView.Adapter<JobAdapterForAI.JobViewHolder> {

    private final List<Job> jobList; // Sử dụng Models.Job
    private final Context context;
    private OnCoverLetterClickListener coverLetterClickListener;

    public JobAdapterForAI(Context context, List<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    public void setOnCoverLetterClickListener(OnCoverLetterClickListener listener) {
        this.coverLetterClickListener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_for_ai, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.tvTitle.setText(job.getTitle());
        holder.tvCompany.setText(job.getCompany());
        holder.tvLocation.setText(job.getLocation());
        holder.tvSalary.setText(String.format("%,.0f", job.getSalary())); // Định dạng salary thành String

        // Load logo nếu có
        if (job.getLogoUrl() != null && !job.getLogoUrl().isEmpty()) {
            Picasso.get()
                    .load(job.getLogoUrl())
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.default_logo)
                    .into(holder.logoImageView);
        } else {
            holder.logoImageView.setImageResource(R.drawable.ic_profile);
        }

        // Xử lý nhấn vào công việc
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, JobDetailActivity.class);
            intent.putExtra("job_id", job.getId());
            context.startActivity(intent);
        });

        // Xử lý nhấn vào gợi ý viết thư ứng tuyển
        holder.tvWriteCoverLetter.setOnClickListener(v -> {
            if (coverLetterClickListener != null) {
                coverLetterClickListener.onCoverLetterClick(job);
            }
        });

        // Xử lý nút lưu công việc (tùy chọn)
        holder.btnSaveJob.setOnClickListener(v -> {
            // TODO: Xử lý lưu công việc nếu cần
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCompany, tvLocation, tvSalary, tvWriteCoverLetter;
        ImageView logoImageView;
        ImageButton btnSaveJob;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvWriteCoverLetter = itemView.findViewById(R.id.tvWriteCoverLetter);
            logoImageView = itemView.findViewById(R.id.logoImageView);
            btnSaveJob = itemView.findViewById(R.id.btnSaveJob);
        }
    }

    public interface OnCoverLetterClickListener {
        void onCoverLetterClick(Job job); // Sử dụng Models.Job
    }
}