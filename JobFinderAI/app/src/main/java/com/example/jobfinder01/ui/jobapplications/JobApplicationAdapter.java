package com.example.jobfinder01.ui.jobapplications;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder01.R;
import com.example.jobfinder01.ui.home.JobDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JobApplicationAdapter extends RecyclerView.Adapter<JobApplicationAdapter.ViewHolder> {

    private List<JobApplication> applicationList;

    public JobApplicationAdapter(List<JobApplication> applicationList) {
        this.applicationList = applicationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobApplication application = applicationList.get(position);
        holder.tvJobTitle.setText(application.getJobTitle());
        holder.tvCompany.setText(application.getCompany());

        // Định dạng ngày ứng tuyển
        String appliedDate = application.getCreatedAt();
        if (appliedDate != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormat.parse(appliedDate);
                holder.tvAppliedDate.setText("Ứng tuyển: " + outputFormat.format(date));
            } catch (ParseException e) {
                holder.tvAppliedDate.setText("Ứng tuyển: " + appliedDate);
            }
        } else {
            holder.tvAppliedDate.setText("Ứng tuyển: Không có thông tin");
        }

        // Chuyển đổi trạng thái từ API thành tiếng Việt
        String statusDisplay;
        switch (application.getStatus()) {
            case "pending":
                statusDisplay = "Đang chờ";
                holder.tvStatus.setBackgroundResource(R.drawable.status_pending);
                break;
            case "approved":
                statusDisplay = "Đã duyệt";
                holder.tvStatus.setBackgroundResource(R.drawable.status_approved);
                break;
            case "rejected":
                statusDisplay = "Bị từ chối";
                holder.tvStatus.setBackgroundResource(R.drawable.status_rejected);
                break;
            default:
                statusDisplay = application.getStatus();
                holder.tvStatus.setBackgroundResource(R.drawable.status_background);
        }
        holder.tvStatus.setText(statusDisplay);

        // Xử lý nút Xem chi tiết
        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), JobDetailActivity.class);
            intent.putExtra("job_id", application.getJobId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCompanyLogo;
        TextView tvJobTitle, tvCompany, tvAppliedDate, tvStatus;
        com.google.android.material.button.MaterialButton btnViewDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCompanyLogo = itemView.findViewById(R.id.ivCompanyLogo);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvAppliedDate = itemView.findViewById(R.id.tvAppliedDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}