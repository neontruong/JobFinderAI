package com.example.jobfinder01.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobfinder01.Chat.Message;
import com.example.jobfinder01.R;
import com.example.jobfinder01.ui.home.JobDetailActivity;
import com.example.jobfinder01.Models.Job;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private ArrayList<Message> messageList;
    private Context context;
    private JobAdapterForAI jobAdapter;
    private OnCoverLetterClickListener coverLetterClickListener;

    public ChatAdapter(ArrayList<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    public void setOnCoverLetterClickListener(OnCoverLetterClickListener listener) {
        this.coverLetterClickListener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.isUser()) {
            // Tin nhắn của người dùng
            holder.tvUserMessage.setVisibility(View.VISIBLE);
            holder.botMessageLayout.setVisibility(View.GONE);
            holder.btnOpenFile.setVisibility(View.GONE);
            holder.btnSendEmail.setVisibility(View.GONE);
            if (message.isFileMessage()) {
                holder.tvUserMessage.setText("File: " + message.getText());
            } else {
                holder.tvUserMessage.setText(message.getText());
            }
        } else {
            // Tin nhắn của bot
            holder.tvUserMessage.setVisibility(View.GONE);
            holder.botMessageLayout.setVisibility(View.VISIBLE);

            if (message.isJobMessage()) {
                // Hiển thị danh sách công việc
                holder.tvBotMessage.setText("Dưới đây là các công việc gợi ý phù hợp nhất với bạn:");
                holder.recyclerViewJobs.setVisibility(View.VISIBLE);

                // Khởi tạo JobAdapterForAI và gắn vào RecyclerView
                jobAdapter = new JobAdapterForAI(context, message.getJobs());
                jobAdapter.setOnCoverLetterClickListener(job -> {
                    if (coverLetterClickListener != null) {
                        coverLetterClickListener.onCoverLetterClick(job);
                    }
                });
                holder.recyclerViewJobs.setLayoutManager(new LinearLayoutManager(context));
                holder.recyclerViewJobs.setAdapter(jobAdapter);

                holder.btnOpenFile.setVisibility(View.GONE);
                holder.btnSendEmail.setVisibility(View.GONE);
            } else if (message.isFileMessage()) {
                // Hiển thị tin nhắn có file
                holder.tvBotMessage.setText("File: " + message.getText());
                holder.recyclerViewJobs.setVisibility(View.GONE);
                holder.btnOpenFile.setVisibility(View.VISIBLE);
                holder.btnOpenFile.setOnClickListener(v -> {
                    // Xử lý mở file
                });
                holder.btnSendEmail.setVisibility(View.GONE);
            } else {
                // Hiển thị tin nhắn văn bản thông thường
                // Xử lý gạch đầu dòng bằng SpannableString
                String text = message.getText();
                SpannableString spannableString = new SpannableString(text);
                String[] lines = text.split("\n");
                int currentIndex = 0;

                for (String line : lines) {
                    if (line.trim().startsWith("-")) {
                        int start = currentIndex + line.indexOf("-");
                        int end = currentIndex + line.length();
                        // Thêm BulletSpan với khoảng cách thụt lề 20px và kích thước bullet 10px
                        spannableString.setSpan(new BulletSpan(20, context.getResources().getColor(android.R.color.black), 10), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    currentIndex += line.length() + 1; // +1 cho ký tự xuống dòng
                }

                holder.tvBotMessage.setText(spannableString);
                holder.recyclerViewJobs.setVisibility(View.GONE);
                holder.btnOpenFile.setVisibility(View.GONE);

                // Nếu là thư ứng tuyển, hiển thị nút gửi email
                if (message.getText().contains("Thư ứng tuyển")) {
                    holder.btnSendEmail.setVisibility(View.VISIBLE);
                    holder.btnSendEmail.setOnClickListener(v -> {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{message.getContactEmail()});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Thư ứng tuyển: " + message.getJobTitle());
                        // Loại bỏ dòng tiêu đề "Thư ứng tuyển: ..." khỏi nội dung email
                        String emailBody = message.getText();
                        if (emailBody.startsWith("Thư ứng tuyển: ")) {
                            emailBody = emailBody.substring(emailBody.indexOf("\n") + 1);
                        }
                        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
                        context.startActivity(Intent.createChooser(emailIntent, "Gửi email"));
                    });
                } else {
                    holder.btnSendEmail.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserMessage, tvBotMessage;
        LinearLayout botMessageLayout;
        RecyclerView recyclerViewJobs;
        Button btnOpenFile, btnSendEmail;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
            tvBotMessage = itemView.findViewById(R.id.tvBotMessage);
            botMessageLayout = itemView.findViewById(R.id.botMessageLayout);
            recyclerViewJobs = itemView.findViewById(R.id.recyclerViewJobs);
            btnOpenFile = itemView.findViewById(R.id.btnOpenFile);
            btnSendEmail = itemView.findViewById(R.id.btnSendEmail);
        }
    }

    public interface OnCoverLetterClickListener {
        void onCoverLetterClick(com.example.jobfinder01.Models.Job job);
    }
}