package com.example.jobfinder01.CV;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobfinder01.API.ApiClient;
import com.example.jobfinder01.API.ApiService;
import com.example.jobfinder01.R;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CVAdapter extends RecyclerView.Adapter<CVAdapter.ViewHolder> {
    private List<CV> list;
    private Context context;

    public CVAdapter(Context context, List<CV> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CV cv = list.get(position);

        // Hiển thị thông tin với kiểm tra null
        if (holder.tvFullName != null) {
            holder.tvFullName.setText(cv.getFullName() != null ? cv.getFullName() : "N/A");
        } else {
            Log.e("CVAdapter", "tvFullName is null at position: " + position);
        }

        if (holder.tvJobTitle != null) {
            holder.tvJobTitle.setText(cv.getJobTitle() != null ? cv.getJobTitle() : "N/A");
        } else {
            Log.e("CVAdapter", "tvJobTitle is null at position: " + position);
        }

        if (holder.tvWorkArea != null) {
            holder.tvWorkArea.setText(cv.getWorkArea() != null ? cv.getWorkArea() : "N/A");
        } else {
            Log.e("CVAdapter", "tvWorkArea is null at position: " + position);
        }

        if (holder.tvSalary != null) {
            holder.tvSalary.setText(cv.getSalary() != null ? cv.getSalary() : "N/A");
        } else {
            Log.e("CVAdapter", "tvSalary is null at position: " + position);
        }

        // Định dạng thời gian created_at
        String createdAt = cv.getCreatedAt();
        if (holder.tvCreatedAt != null) {
            if (createdAt != null) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    Date date = inputFormat.parse(createdAt);
                    holder.tvCreatedAt.setText(outputFormat.format(date));
                } catch (Exception e) {
                    holder.tvCreatedAt.setText(createdAt);
                }
            } else {
                holder.tvCreatedAt.setText("N/A");
            }
        } else {
            Log.e("CVAdapter", "tvCreatedAt is null at position: " + position);
        }

        // Xử lý sự kiện click để gọi API lấy chi tiết CV
        holder.itemView.setOnClickListener(v -> {
            Log.d("CVAdapter", "Fetching CV with ID: " + cv.getId());
            ApiService apiService = ApiClient.getAuthClient(context).create(ApiService.class);
            Call<CVCreateResponse> call = apiService.getCVDetail(cv.getId());
            call.enqueue(new Callback<CVCreateResponse>() {
                @Override
                public void onResponse(Call<CVCreateResponse> call, Response<CVCreateResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CV detailedCV = response.body().getData();
                        Intent intent = new Intent(context, CVDetailActivity.class);
                        intent.putExtra("cv", (Serializable) detailedCV);
                        context.startActivity(intent);
                    } else {
                        String errorMessage = "Lỗi khi lấy chi tiết CV: " + response.code();
                        if (response.code() == 404) {
                            errorMessage = "Không tìm thấy CV với ID: " + cv.getId();
                        } else if (response.code() == 401) {
                            errorMessage = "Không có quyền truy cập. Vui lòng đăng nhập lại.";
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("CVAdapter", "Error: " + errorMessage);
                    }
                }

                @Override
                public void onFailure(Call<CVCreateResponse> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("CVAdapter", "Connection error: " + t.getMessage());
                }
            });
        });

        // Xử lý nút More (tùy chọn)
        if (holder.btnMore != null) {
            holder.btnMore.setOnClickListener(v -> {
                Toast.makeText(context, "More options for CV: " + cv.getJobTitle(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("CVAdapter", "btnMore is null at position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0; // Thêm kiểm tra null cho list
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAvatar;
        TextView tvFullName, tvJobTitle, tvWorkArea, tvSalary, tvCreatedAt;
        ImageButton btnMore;

        public ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvWorkArea = itemView.findViewById(R.id.tvWorkArea);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            btnMore = itemView.findViewById(R.id.btnMore);

            // Debug để kiểm tra xem view có null không
            if (tvFullName == null) Log.e("CVAdapter", "tvFullName is null in ViewHolder");
            if (tvJobTitle == null) Log.e("CVAdapter", "tvJobTitle is null in ViewHolder");
            if (tvWorkArea == null) Log.e("CVAdapter", "tvWorkArea is null in ViewHolder");
            if (tvSalary == null) Log.e("CVAdapter", "tvSalary is null in ViewHolder");
            if (tvCreatedAt == null) Log.e("CVAdapter", "tvCreatedAt is null in ViewHolder");
            if (ivAvatar == null) Log.e("CVAdapter", "ivAvatar is null in ViewHolder");
            if (btnMore == null) Log.e("CVAdapter", "btnMore is null in ViewHolder");
        }
    }
}