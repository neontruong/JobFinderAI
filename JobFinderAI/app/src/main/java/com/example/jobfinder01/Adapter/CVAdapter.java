//package com.example.jobfinder01.Adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.jobfinder01.R;
//
//import java.util.List;
//
//public class CVAdapter extends RecyclerView.Adapter<CVAdapter.CVViewHolder> {
//
//    private final List<Uri> cvList;
//    private final Context context;
//
//    public CVAdapter(List<Uri> cvList, Context context) {
//        this.cvList = cvList;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public CVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cv, parent, false);
//        return new CVViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CVViewHolder holder, int position) {
//        Uri fileUri = cvList.get(position);
//        // Hiển thị tên file trong tvCVName
//        holder.tvCVName.setText(fileUri.getLastPathSegment());
//        // Các trường khác để trống hoặc hiển thị dữ liệu mặc định
//        holder.tvUploadDate.setText("N/A"); // Có thể thay bằng ngày thực nếu có dữ liệu
//        holder.tvFileType.setText(getFileType(fileUri)); // Xác định loại file
//
//        // Xử lý sự kiện nhấn để mở file
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(fileUri, "*/*");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            try {
//                context.startActivity(intent);
//            } catch (Exception e) {
//                Toast.makeText(context, "Không thể mở file", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Xử lý nút More (tùy chọn)
//        holder.btnMore.setOnClickListener(v -> {
//            Toast.makeText(context, "Tùy chọn cho: " + fileUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
//            // Thêm logic nếu cần (xóa CV, v.v.)
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return cvList.size();
//    }
//
//    // Phương thức phụ để lấy loại file từ Uri
//    private String getFileType(Uri uri) {
//        String path = uri.getLastPathSegment();
//        if (path != null) {
//            if (path.endsWith(".pdf")) return "PDF";
//            if (path.endsWith(".doc") || path.endsWith(".docx")) return "DOC";
//            if (path.endsWith(".png")) return "PNG";
//            if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "JPG";
//        }
//        return "Unknown";
//    }
//
//    static class CVViewHolder extends RecyclerView.ViewHolder {
//        TextView tvCVName, tvUploadDate, tvFileType;
//        ImageButton btnMore;
//
//        public CVViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvCVName = itemView.findViewById(R.id.tvCVName);
//            tvUploadDate = itemView.findViewById(R.id.tvUploadDate);
//            tvFileType = itemView.findViewById(R.id.tvFileType);
//            btnMore = itemView.findViewById(R.id.btnMore);
//        }
//    }
//}