package com.example.jobfinder01.Chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobfinder01.API.ApiClient;
import com.example.jobfinder01.Chat.ChatApiService;
import com.example.jobfinder01.API.GeminiApiService;
import com.example.jobfinder01.Chat.ProfileResponse;
import com.example.jobfinder01.Models.UserProfile;
import com.example.jobfinder01.Adapter.ChatAdapter;
import com.example.jobfinder01.LoginActivity;
import com.example.jobfinder01.R;
import com.example.jobfinder01.RequestBody;
import com.example.jobfinder01.ResponseBody;
import com.example.jobfinder01.Models.Job;
import com.google.gson.Gson;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class ChatActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messageList;
    private EditText etMessage;
    private ImageButton btnSend, btnAttach;
    private GeminiApiService geminiApiService;
    private ChatApiService chatApiService;
    private boolean isWaitingForResponse = false;
    private int loadingMessagePosition = -1;
    private Handler handler;
    private boolean isCvContext = false;
    private UserProfile userProfile;
    private boolean isProfileLoaded = false;
    private Job currentJob; // Lưu Job hiện tại khi tạo thư ứng tuyển

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnAttach = findViewById(R.id.btnAttach);

        handler = new Handler(Looper.getMainLooper());

        String token = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("auth_token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Retrofit geminiRetrofit = new Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new okhttp3.OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .build())
                .build();
        geminiApiService = geminiRetrofit.create(GeminiApiService.class);

        chatApiService = ApiClient.getAuthClient(this).create(ChatApiService.class);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, this);
        chatAdapter.setOnCoverLetterClickListener(this::requestCoverLetter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        loadUserProfile();

        loadChatHistory();

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                Message userMessage = new Message(text, true, null);
                messageList.add(userMessage);
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                saveChatToServer(userMessage);
                etMessage.setText("");

                if (!isWaitingForResponse) {
                    isWaitingForResponse = true;
                    Message loadingMessage = new Message("Đang xử lý yêu cầu của bạn...", false, null);
                    messageList.add(loadingMessage);
                    loadingMessagePosition = messageList.size() - 1;
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);

                    boolean isCvRelated = isCvRelated(text);
                    boolean isJobRelated = isJobRelated(text);
                    Log.d("ChatActivity", "Message: " + text + ", isCvRelated: " + isCvRelated + ", isJobRelated: " + isJobRelated + ", isCvContext: " + isCvContext);

                    if (isJobRelated) {
                        isCvContext = false;
                        Log.d("ChatActivity", "Job-related query detected, exiting CV context");
                        handler.postDelayed(() -> loadChatHistory(), 3000);
                    } else if (isCvRelated || isCvContext) {
                        if (isCvRelated) {
                            isCvContext = true;
                            Log.d("ChatActivity", "CV-related query detected, entering CV context");
                        }
                        if (!isProfileLoaded) {
                            Log.d("ChatActivity", "Profile not yet loaded, waiting...");
                            handler.postDelayed(() -> processCvRequest(text), 1000);
                        } else {
                            Log.d("ChatActivity", "Sending to Gemini API for CV-related query");
                            sendToGeminiApi(buildCvPrompt(text));
                        }
                    } else {
                        isCvContext = false;
                        Log.d("ChatActivity", "Sending to backend for non-CV, non-job query");
                        handler.postDelayed(() -> loadChatHistory(), 3000);
                    }
                } else {
                    Toast.makeText(this, "Đang xử lý yêu cầu trước đó, vui lòng chờ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAttach.setOnClickListener(v -> openFileChooser());
    }

    private void processCvRequest(String text) {
        if (isProfileLoaded) {
            Log.d("ChatActivity", "Profile loaded, sending to Gemini API for CV-related query");
            sendToGeminiApi(buildCvPrompt(text));
        } else {
            Log.d("ChatActivity", "Profile still not loaded, sending to Gemini API with no profile");
            sendToGeminiApi(buildCvPrompt(text));
            runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Không thể tải profile của bạn. Vui lòng cung cấp thông tin để tạo CV.", Toast.LENGTH_LONG).show());
        }
    }

    private void requestCoverLetter(com.example.jobfinder01.Models.Job job) {
        Log.d("ChatActivity", "requestCoverLetter called for job: " + job.getTitle());
        if (!isWaitingForResponse) {
            Log.d("ChatActivity", "isWaitingForResponse is false, proceeding...");
            isWaitingForResponse = true;
            Message loadingMessage = new Message("Đang tạo thư ứng tuyển...", false, null);
            messageList.add(loadingMessage);
            loadingMessagePosition = messageList.size() - 1;
            Log.d("ChatActivity", "Added loading message at position: " + loadingMessagePosition);
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);

            currentJob = job; // Lưu Job hiện tại
            String prompt = buildCoverLetterPrompt(job);
            Log.d("ChatActivity", "Prompt created: " + prompt);
            sendToGeminiApi(prompt);
        } else {
            Log.d("ChatActivity", "isWaitingForResponse is true, showing toast");
            Toast.makeText(this, "Đang xử lý yêu cầu trước đó, vui lòng chờ", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserProfile() {
        Log.d("ChatActivity", "Starting to load user profile...");
        String token = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("auth_token", "");
        Log.d("ChatActivity", "Auth token: " + token);
        chatApiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                Log.d("ChatActivity", "Received response from getProfile API: " + response.code());
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    userProfile = response.body().getData();
                    isProfileLoaded = true;
                    Log.d("ChatActivity", "User profile loaded successfully: " + new Gson().toJson(userProfile));
                } else {
                    Log.e("ChatActivity", "Failed to load user profile. Response code: " + response.code());
                    if (response.code() == 404) {
                        Log.d("ChatActivity", "Profile not found for user. Please create a profile.");
                        runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Bạn chưa tạo profile. Vui lòng cập nhật profile để tạo CV tốt hơn.", Toast.LENGTH_LONG).show());
                    } else {
                        Log.e("ChatActivity", "Unexpected response: " + (response.errorBody() != null ? response.errorBody().toString() : "No error body"));
                    }
                    userProfile = null;
                    isProfileLoaded = true;
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("ChatActivity", "Error loading user profile: " + t.getMessage());
                userProfile = null;
                isProfileLoaded = true;
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Lỗi khi tải profile: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private boolean isCvRelated(String message) {
        String normalizedMessage = normalizeString(message);
        Log.d("ChatActivity", "Normalized message for CV check: " + normalizedMessage);

        Pattern cvPattern = Pattern.compile(
                "(giup.*lam.*cv|giup.*tao.*cv|toi.*uu.*cv|huong.*dan.*lam.*cv|cach.*lam.*cv|lam.*cv|create.*cv|tao.*cv|optimize.*cv|goi.*y.*cv)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );
        Matcher matcher = cvPattern.matcher(normalizedMessage);
        boolean result = matcher.find();
        if (result) {
            Log.d("ChatActivity", "CV pattern matched: " + matcher.group());
        }
        return result;
    }

    private boolean isJobRelated(String message) {
        String normalizedMessage = normalizeString(message);
        Log.d("ChatActivity", "Normalized message for job check: " + normalizedMessage);

        Pattern jobPattern = Pattern.compile(
                "(toi.*muon.*tim.*viec|tim.*viec|goi.*y.*viec.*lam)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );
        Matcher matcher = jobPattern.matcher(normalizedMessage);
        boolean result = matcher.find();
        if (result) {
            Log.d("ChatActivity", "Job pattern matched: " + matcher.group());
        }
        return result;
    }

    private String normalizeString(String input) {
        String normalized = input.toLowerCase();
        normalized = normalized.replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a")
                .replaceAll("[éèẻẽẹêếềểễệ]", "e")
                .replaceAll("[íìỉĩị]", "i")
                .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
                .replaceAll("[úùủũụưứừửữự]", "u")
                .replaceAll("[ýỳỷỹỵ]", "y")
                .replaceAll("đ", "d");
        normalized = normalized.replaceAll("\\s+", " ");
        return normalized.trim();
    }

    private String buildCvPrompt(String message) {
        StringBuilder context = new StringBuilder();
        int startIndex = Math.max(0, messageList.size() - 5);
        for (int i = startIndex; i < messageList.size(); i++) {
            Message msg = messageList.get(i);
            context.append(msg.isUser() ? "User: " : "Bot: ")
                    .append(msg.getText())
                    .append("\n");
        }

        StringBuilder profileInfo = new StringBuilder();
        if (userProfile != null) {
            profileInfo.append("Thông tin profile của người dùng:\n");
            if (userProfile.getFullName() != null) {
                profileInfo.append("Họ tên: ").append(userProfile.getFullName()).append("\n");
            }
            if (userProfile.getEmail() != null) {
                profileInfo.append("Email: ").append(userProfile.getEmail()).append("\n");
            }
            if (userProfile.getPhone() != null) {
                profileInfo.append("Số điện thoại: ").append(userProfile.getPhone()).append("\n");
            }
            if (userProfile.getSkills() != null) {
                profileInfo.append("Kỹ năng: ").append(userProfile.getSkills()).append("\n");
            }
            if (userProfile.getExperience() != null) {
                profileInfo.append("Kinh nghiệm: ").append(userProfile.getExperience()).append("\n");
            }
            if (userProfile.getEducation() != null) {
                profileInfo.append("Học vấn: ").append(userProfile.getEducation()).append("\n");
            }
            if (userProfile.getLocation() != null) {
                profileInfo.append("Địa điểm: ").append(userProfile.getLocation()).append("\n");
            }
            if (userProfile.getSalary() != null) {
                profileInfo.append("Mức lương mong muốn: ").append(userProfile.getSalary()).append("\n");
            }
        } else {
            profileInfo.append("Không có thông tin profile của người dùng.\n");
        }

        Log.d("ChatActivity", "Building CV prompt with profile info: " + profileInfo.toString());

        return "Bạn là một trợ lý chuyên về tạo và tối ưu CV. Dựa trên thông tin sau:\n" +
                profileInfo.toString() +
                "Ngữ cảnh trò chuyện:" +
                context.toString() +
                "Câu hỏi hiện tại: " + message +
                "Hãy trả lời một cách tự nhiên, chi tiết và phù hợp với ngữ cảnh. Sử dụng thông tin từ profile (nếu có) để tạo hoặc tối ưu CV. Nếu thông tin trong profile chưa đủ, hãy yêu cầu người dùng cung cấp thêm. Nếu người dùng cung cấp thông tin mới trong trò chuyện, ưu tiên sử dụng thông tin đó. Trả lời bằng tiếng Việt.";
    }

    private String buildCoverLetterPrompt(Job job) {
        StringBuilder profileInfo = new StringBuilder();
        if (userProfile != null) {
            profileInfo.append("Thông tin profile của người dùng:\n");
            if (userProfile.getFullName() != null && !userProfile.getFullName().isEmpty()) {
                profileInfo.append("Họ tên: ").append(userProfile.getFullName()).append("\n");
                Log.d("ChatActivity", "Full name from userProfile: " + userProfile.getFullName());
            } else {
                profileInfo.append("Họ tên: Ứng viên\n");
                Log.d("ChatActivity", "Full name not available, using default: Ứng viên");
            }
            if (userProfile.getEmail() != null && !userProfile.getEmail().isEmpty()) {
                profileInfo.append("Email: ").append(userProfile.getEmail()).append("\n");
                Log.d("ChatActivity", "Email from userProfile: " + userProfile.getEmail());
            } else {
                profileInfo.append("Email: example@email.com\n");
                Log.d("ChatActivity", "Email not available, using default: example@email.com");
            }
            if (userProfile.getPhone() != null && !userProfile.getPhone().isEmpty()) {
                profileInfo.append("Số điện thoại: ").append(userProfile.getPhone()).append("\n");
                Log.d("ChatActivity", "Phone from userProfile: " + userProfile.getPhone());
            } else {
                profileInfo.append("Số điện thoại: 0123 456 789\n");
                Log.d("ChatActivity", "Phone not available, using default: 0123 456 789");
            }
            if (userProfile.getSkills() != null && !userProfile.getSkills().isEmpty()) {
                profileInfo.append("Kỹ năng: ").append(userProfile.getSkills()).append("\n");
                Log.d("ChatActivity", "Skills from userProfile: " + userProfile.getSkills());
            } else {
                profileInfo.append("Kỹ năng: [Hãy giả định một số kỹ năng phù hợp với công việc, ví dụ: lập trình Java, Spring Boot, làm việc với cơ sở dữ liệu]\n");
                Log.d("ChatActivity", "Skills not available, using default assumption");
            }
            if (userProfile.getExperience() != null && !userProfile.getExperience().isEmpty()) {
                profileInfo.append("Kinh nghiệm: ").append(userProfile.getExperience()).append("\n");
                profileInfo.append("Số năm kinh nghiệm: 2 năm (bao gồm 1 năm làm PHP Developer và giả định 1 năm làm việc với Java để đáp ứng yêu cầu công việc)\n");
                Log.d("ChatActivity", "Experience from userProfile: " + userProfile.getExperience());
            } else {
                profileInfo.append("Kinh nghiệm: [Hãy giả định ứng viên có 1-2 năm kinh nghiệm liên quan đến công việc]\n");
                Log.d("ChatActivity", "Experience not available, using default assumption");
            }
            if (userProfile.getEducation() != null && !userProfile.getEducation().isEmpty()) {
                profileInfo.append("Học vấn: ").append(userProfile.getEducation()).append("\n");
                Log.d("ChatActivity", "Education from userProfile: " + userProfile.getEducation());
            } else {
                profileInfo.append("Học vấn: [Hãy giả định ứng viên có bằng Cử nhân Công nghệ Thông tin]\n");
                Log.d("ChatActivity", "Education not available, using default assumption");
            }
            if (userProfile.getLocation() != null && !userProfile.getLocation().isEmpty()) {
                profileInfo.append("Địa điểm: ").append(userProfile.getLocation()).append("\n");
                Log.d("ChatActivity", "Location from userProfile: " + userProfile.getLocation());
            }
            if (userProfile.getSalary() != null && !userProfile.getSalary().isEmpty()) {
                profileInfo.append("Mức lương mong muốn: ").append(userProfile.getSalary()).append(" USD\n");
                Log.d("ChatActivity", "Salary from userProfile: " + userProfile.getSalary() + " USD");
            }
            profileInfo.append("Chuyên ngành: Kỹ thuật phần mềm\n");
        } else {
            profileInfo.append("Không có thông tin profile của người dùng. Hãy giả định ứng viên có:\n");
            profileInfo.append("Họ tên: Ứng viên\n");
            profileInfo.append("Email: example@email.com\n");
            profileInfo.append("Số điện thoại: 0123 456 789\n");
            profileInfo.append("Kỹ năng: [Hãy giả định một số kỹ năng phù hợp với công việc, ví dụ: lập trình Java, Spring Boot, làm việc với cơ sở dữ liệu]\n");
            profileInfo.append("Kinh nghiệm: [Hãy giả định ứng viên có 1-2 năm kinh nghiệm liên quan đến công việc]\n");
            profileInfo.append("Học vấn: [Hãy giả định ứng viên có bằng Cử nhân Công nghệ Thông tin]\n");
            profileInfo.append("Chuyên ngành: [Hãy giả định chuyên ngành là Công nghệ Thông tin]\n");
            Log.d("ChatActivity", "userProfile is null, using default assumptions");
        }

        StringBuilder jobInfo = new StringBuilder();
        jobInfo.append("Thông tin công việc:\n");
        jobInfo.append("Tiêu đề: ").append(job.getTitle()).append("\n");
        jobInfo.append("Công ty: ").append(job.getCompany()).append("\n");
        jobInfo.append("Địa điểm: ").append(job.getLocation()).append("\n");
        jobInfo.append("Mức lương: ").append(job.getSalary()).append("\n");
        if (job.getContactEmail() != null) {
            jobInfo.append("Email liên hệ: ").append(job.getContactEmail()).append("\n");
        }
        if (job.getDescription() != null) {
            jobInfo.append("Mô tả công việc: ").append(job.getDescription()).append("\n");
        }
        if (job.getRequirements() != null) {
            jobInfo.append("Yêu cầu công việc: ").append(job.getRequirements()).append("\n");
        }
        if (job.getCompanyDescription() != null) {
            jobInfo.append("Mô tả công ty: ").append(job.getCompanyDescription()).append("\n");
        }
        jobInfo.append("Nguồn tuyển dụng: Qua nền tảng ứng dụng Jobfinder AI\n");

        return "Bạn là một trợ lý chuyên viết thư ứng tuyển. Dựa trên thông tin sau:\n" +
                profileInfo.toString() +
                jobInfo.toString() +
                "Hãy viết một lá thư ứng tuyển chuyên nghiệp bằng tiếng Việt cho công việc trên. Thư cần có tiêu đề 'Thư ứng tuyển: [Tên công việc]' ở dòng đầu tiên, sau đó xuống dòng để bắt đầu nội dung chính. Nội dung chính phải được chia thành các đoạn ngắn (3-4 câu mỗi đoạn), mỗi đoạn tập trung vào một ý chính (giới thiệu bản thân, học vấn và kinh nghiệm, kỹ năng, lý do ứng tuyển, mong muốn phỏng vấn, và kết thúc). Sử dụng gạch đầu dòng (dấu '-') để liệt kê các kỹ năng hoặc kinh nghiệm nổi bật, mỗi mục trên một dòng riêng, giúp thư dễ đọc và rõ ràng hơn. Đề cập đến tất cả kỹ năng từ profile (bao gồm cả những kỹ năng không trực tiếp liên quan đến công việc) để thể hiện sự đa dạng trong năng lực. Đề cập đến học vấn và chuyên ngành từ profile để tăng tính thuyết phục. Nếu mức lương được cung cấp trong profile, hãy ghi rõ đơn vị tiền tệ là USD (ký hiệu $) và không được chuyển đổi sang đơn vị khác (ví dụ: nếu mức lương là 500, hãy ghi là 500 USD). Trình bày mức lương mong muốn một cách chuyên nghiệp, ví dụ: 'Tôi mong muốn mức lương 500 USD, tuy nhiên, tôi sẵn sàng thảo luận thêm để phù hợp với chính sách của công ty.' Nếu thiếu thông tin (như tên công ty trước đây hoặc điểm ấn tượng về công ty), hãy bỏ qua các phần đó và không nhắc đến chúng trong thư. Nếu thiếu thông tin về kinh nghiệm hoặc kỹ năng, hãy giả định hợp lý dựa trên yêu cầu công việc (ví dụ: giả định ứng viên có kỹ năng lập trình Java, Spring Boot nếu công việc yêu cầu). Sử dụng thông tin từ mô tả công ty (nếu có) để bày tỏ sự ấn tượng một cách cụ thể. Bắt đầu nội dung chính của thư với dòng 'Kính gửi Ban tuyển dụng [Tên công ty], Qua nền tảng ứng dụng Jobfinder AI, tôi được biết đến vị trí [Tên công việc] tại công ty [Tên công ty] và rất hào hứng ứng tuyển.' Kết thúc thư bằng lời cảm ơn và chữ ký với tên người dùng, số điện thoại và email (sử dụng thông tin từ profile hoặc thông tin mặc định nếu không có). Định dạng chữ ký sao cho mỗi thông tin (tên, số điện thoại, email) nằm trên một dòng riêng, ví dụ:\nTrân trọng,\n[Tên]\n[Số điện thoại]\n[Email]\nĐừng để lại bất kỳ phần nào yêu cầu người dùng cung cấp thêm thông tin trong thư.";
    }

    private void loadChatHistory() {
        chatApiService.getChatHistory().enqueue(new Callback<ChatHistoryResponse>() {
            @Override
            public void onResponse(Call<ChatHistoryResponse> call, Response<ChatHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatMessage> chatMessages = response.body().getData();
                    if (loadingMessagePosition != -1 && loadingMessagePosition < messageList.size()) {
                        messageList.remove(loadingMessagePosition);
                        chatAdapter.notifyItemRemoved(loadingMessagePosition);
                        loadingMessagePosition = -1;
                    }

                    messageList.clear();
                    for (ChatMessage chatMessage : chatMessages) {
                        if (!chatMessage.isUser() && chatMessage.getMessage().startsWith("{")) {
                            try {
                                Gson gson = new Gson();
                                JobListResponse jobListResponse = gson.fromJson(chatMessage.getMessage(), JobListResponse.class);
                                if (jobListResponse != null && jobListResponse.getJobs() != null) {
                                    messageList.add(new Message("Danh sách công việc gợi ý:", false, null, jobListResponse.getJobs()));
                                } else {
                                    messageList.add(new Message(chatMessage.getMessage(), chatMessage.isUser(), chatMessage.getFileUriAsUri()));
                                }
                            } catch (Exception e) {
                                messageList.add(new Message("Lỗi khi phân tích dữ liệu: " + e.getMessage(), false, null));
                            }
                        } else {
                            messageList.add(new Message(chatMessage.getMessage(), chatMessage.isUser(), chatMessage.getFileUriAsUri()));
                        }
                    }
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);
                } else {
                    runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Không thể tải lịch sử chat: " + response.code(), Toast.LENGTH_SHORT).show());
                    if (response.code() == 401) {
                        redirectToLogin();
                    }
                }
                isWaitingForResponse = false;
            }

            @Override
            public void onFailure(Call<ChatHistoryResponse> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                if (loadingMessagePosition != -1 && loadingMessagePosition < messageList.size()) {
                    messageList.remove(loadingMessagePosition);
                    chatAdapter.notifyItemRemoved(loadingMessagePosition);
                    loadingMessagePosition = -1;
                }
                isWaitingForResponse = false;
            }
        });
    }

    private void saveChatToServer(Message message) {
        ChatRequest chatRequest = new ChatRequest(
                message.getText(),
                message.isUser(),
                message.getFileUri() != null ? message.getFileUri().toString() : null
        );

        chatApiService.saveChat(chatRequest).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (!response.isSuccessful() && response.code() == 401) {
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                isWaitingForResponse = false;
                if (loadingMessagePosition != -1 && loadingMessagePosition < messageList.size()) {
                    messageList.remove(loadingMessagePosition);
                    chatAdapter.notifyItemRemoved(loadingMessagePosition);
                    loadingMessagePosition = -1;
                }
            }
        });
    }

    private void redirectToLogin() {
        Toast.makeText(this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Chọn file"), PICK_FILE_REQUEST);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở trình chọn file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                Message userMessage = new Message("File đính kèm", true, fileUri);
                messageList.add(userMessage);
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                saveChatToServer(userMessage);

                try {
                    String fileContent = extractFileContent(fileUri);
                    if (fileContent != null && !fileContent.isEmpty()) {
                        if (!isWaitingForResponse) {
                            isWaitingForResponse = true;
                            Message botMessage = new Message("Đang xử lý yêu cầu của bạn...", false, null);
                            messageList.add(botMessage);
                            loadingMessagePosition = messageList.size() - 1;
                            chatAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.scrollToPosition(messageList.size() - 1);
                            isCvContext = true;
                            sendToGeminiApi("Phân tích CV này và đưa ra gợi ý tối ưu: \n" + fileContent);
                        } else {
                            Toast.makeText(this, "Đang xử lý yêu cầu trước đó, vui lòng chờ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Message botMessage = new Message("Không thể trích xuất nội dung từ file", false, null);
                        messageList.add(botMessage);
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                        saveChatToServer(botMessage);
                    }
                } catch (Exception e) {
                    Message botMessage = new Message("Lỗi khi xử lý file: " + e.getMessage(), false, null);
                    messageList.add(botMessage);
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    saveChatToServer(botMessage);
                }
            } else {
                Toast.makeText(this, "Không thể lấy file, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không chọn được file", Toast.LENGTH_SHORT).show();
        }
    }

    private String extractFileContent(Uri fileUri) throws Exception {
        String mimeType = getContentResolver().getType(fileUri);
        String fileContent = "";
        if (mimeType != null) {
            if (mimeType.equals("application/pdf")) {
                fileContent = extractFromPdf(fileUri);
            } else if (mimeType.equals("application/msword") || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                fileContent = extractFromWord(fileUri);
            } else {
                throw new Exception("Định dạng file không được hỗ trợ. Chỉ hỗ trợ PDF và Word.");
            }
        } else {
            throw new Exception("Không thể xác định định dạng file. Vui lòng thử lại.");
        }
        return fileContent;
    }

    private String extractFromPdf(Uri fileUri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(fileUri);
        if (inputStream == null) {
            throw new Exception("Không thể mở file PDF");
        }
        PdfReader reader = new PdfReader(inputStream);
        PdfDocument pdfDocument = new PdfDocument(reader);
        StringBuilder content = new StringBuilder();
        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
            content.append(PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i)));
        }
        pdfDocument.close();
        reader.close();
        inputStream.close();
        return content.toString();
    }

    private String extractFromWord(Uri fileUri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(fileUri);
        if (inputStream == null) {
            throw new Exception("Không thể mở file Word");
        }
        XWPFDocument document = new XWPFDocument(inputStream);
        StringBuilder content = new StringBuilder();
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            content.append(paragraph.getText()).append("\n");
        }
        document.close();
        inputStream.close();
        return content.toString();
    }

    private String removeAsterisks(String text) {
        String cleanedText = text.replaceAll("\\*+", "");
        cleanedText = cleanedText.replaceAll("\\s+", " ");
        return cleanedText.trim();
    }

    private void sendToGeminiApi(String text) {
        Log.d("ChatActivity", "sendToGeminiApi called with text: " + text);
        RequestBody customRequest = new RequestBody(text);
        Gson gson = new Gson();
        String jsonBody = gson.toJson(customRequest);
        Log.d("ChatActivity", "Request JSON: " + jsonBody);
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json"), jsonBody);

        geminiApiService.getResponse(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("ChatActivity", "Gemini API response received, code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String result = response.body().getCandidates()
                                .get(0)
                                .getContent()
                                .getParts()
                                .get(0)
                                .getText();
                        Log.d("ChatActivity", "API response text: " + result);
                        result = removeAsterisks(result);

                        if (loadingMessagePosition != -1 && loadingMessagePosition < messageList.size()) {
                            messageList.remove(loadingMessagePosition);
                            chatAdapter.notifyItemRemoved(loadingMessagePosition);
                            loadingMessagePosition = -1;
                        }

                        String jobTitle = result.contains("Thư ứng tuyển") ? result.split("Thư ứng tuyển: ")[1].split("\n")[0] : "";
                        // Sử dụng email của nhà tuyển dụng từ currentJob
                        String contactEmail = currentJob != null && currentJob.getContactEmail() != null ? currentJob.getContactEmail() : "contact@example.com";
                        Log.d("ChatActivity", "Contact email for job: " + contactEmail);

                        Message botMessage = new Message(result, false, null, jobTitle, contactEmail);
                        messageList.add(botMessage);
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                        saveChatToServer(botMessage);

                        currentJob = null; // Xóa sau khi sử dụng
                    } catch (Exception e) {
                        Log.e("ChatActivity", "Error parsing API response: " + e.getMessage(), e);
                        if (loadingMessagePosition != -1 && loadingMessagePosition < messageList.size()) {
                            messageList.remove(loadingMessagePosition);
                            chatAdapter.notifyItemRemoved(loadingMessagePosition);
                            loadingMessagePosition = -1;
                        }

                        Message botMessage = new Message("Lỗi xử lý phản hồi: " + e.getMessage(), false, null);
                        messageList.add(botMessage);
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                        saveChatToServer(botMessage);
                    }
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("ChatActivity", "API error: " + response.code() + " - " + error);
                        if (loadingMessagePosition != -1 && loadingMessagePosition < messageList.size()) {
                            messageList.remove(loadingMessagePosition);
                            chatAdapter.notifyItemRemoved(loadingMessagePosition);
                            loadingMessagePosition = -1;
                        }

                        Message botMessage = new Message("Lỗi: " + response.code() + " - " + error, false, null);
                        messageList.add(botMessage);
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                        saveChatToServer(botMessage);
                    } catch (Exception e) {
                        Log.e("ChatActivity", "Error handling API error response: " + e.getMessage(), e);
                        if (loadingMessagePosition != -1 && loadingMessagePosition < messageList.size()) {
                            messageList.remove(loadingMessagePosition);
                            chatAdapter.notifyItemRemoved(loadingMessagePosition);
                            loadingMessagePosition = -1;
                        }

                        Message botMessage = new Message("Lỗi: " + response.code(), false, null);
                        messageList.add(botMessage);
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                        saveChatToServer(botMessage);
                    }
                }
                isWaitingForResponse = false;
                Log.d("ChatActivity", "isWaitingForResponse set to false");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ChatActivity", "API call failed: " + t.getMessage(), t);
                if (loadingMessagePosition != -1 && loadingMessagePosition < messageList.size()) {
                    messageList.remove(loadingMessagePosition);
                    chatAdapter.notifyItemRemoved(loadingMessagePosition);
                    loadingMessagePosition = -1;
                }

                Message botMessage = new Message("Lỗi kết nối: " + t.getMessage(), false, null);
                messageList.add(botMessage);
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size());
                saveChatToServer(botMessage);
                isWaitingForResponse = false;
                Log.d("ChatActivity", "isWaitingForResponse set to false after failure");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}