package com.example.jobfinder01.ui.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobfinder01.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Ánh xạ RecyclerView
        rvNotifications = view.findViewById(R.id.rvNotifications);

        // Khởi tạo danh sách thông báo
        notificationList = new ArrayList<>();

        // Thiết lập adapter cho RecyclerView
        adapter = new NotificationAdapter(notificationList);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.setAdapter(adapter);

        // Load thông báo từ SharedPreferences
        loadNotifications();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại thông báo mỗi khi fragment được hiển thị
        loadNotifications();
    }

    private void loadNotifications() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", requireActivity().MODE_PRIVATE);
        String notificationsJson = prefs.getString("notifications", "[]");
        Log.d("NotificationsFragment", "Loaded notifications JSON: " + notificationsJson);

        List<Notification> notifications = new ArrayList<>();
        try {
            notifications = new Gson().fromJson(notificationsJson, new TypeToken<List<Notification>>(){}.getType());
            if (notifications == null) {
                notifications = new ArrayList<>();
            }
        } catch (Exception e) {
            Log.e("NotificationsFragment", "Error parsing notifications: " + e.getMessage());
            e.printStackTrace();
            notifications = new ArrayList<>();
        }

        // Cập nhật danh sách thông báo
        notificationList.clear();
        notificationList.addAll(notifications);
        adapter.notifyDataSetChanged();
        Log.d("NotificationsFragment", "Notifications count: " + notificationList.size());
    }

    // Phương thức để thêm thông báo mới
    public void addNotification(String message) {
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        Notification notification = new Notification(message, timestamp);
        notificationList.add(notification);
        adapter.notifyDataSetChanged();

        // Lưu lại danh sách thông báo
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", requireActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("notifications", new Gson().toJson(notificationList));
        editor.apply();
        Log.d("NotificationsFragment", "Saved notifications: " + new Gson().toJson(notificationList));
    }

    // Adapter cho RecyclerView
    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
        private final List<Notification> notifications;

        public NotificationAdapter(List<Notification> notifications) {
            this.notifications = notifications;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Notification notification = notifications.get(position);
            holder.tvNotificationContent.setText(notification.message); // Dùng tvNotificationContent thay cho tvMessage
            holder.tvTime.setText(notification.timestamp);

            // Xử lý sự kiện xóa
            holder.btnDelete.setOnClickListener(v -> {
                notifications.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, notifications.size());

                // Lưu lại danh sách thông báo sau khi xóa
                SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", requireActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("notifications", new Gson().toJson(notifications));
                editor.apply();
                Log.d("NotificationsFragment", "Deleted notification, new list: " + new Gson().toJson(notifications));
            });
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNotificationContent, tvTime; // Thay tvMessage bằng tvNotificationContent
            Button btnDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                tvNotificationContent = itemView.findViewById(R.id.tvNotificationContent); // Ánh xạ mới
                tvTime = itemView.findViewById(R.id.tvTime); // Đổi từ tvNotificationTime thành tvTime
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}