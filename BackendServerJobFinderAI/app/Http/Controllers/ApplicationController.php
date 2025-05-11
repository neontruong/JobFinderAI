<?php
namespace App\Http\Controllers;

use App\Models\Application;
use App\Models\Job;
use App\Models\CV;
use Illuminate\Http\Request;
use App\Mail\ApplicationNotification;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\File;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Support\Facades\Log; 

class ApplicationController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth:sanctum')->only(['store']);
        $this->middleware(['auth', 'admin'])->only(['index']);
    }

    public function store(Request $request)
    {
        $request->validate([
            'job_id' => 'required|exists:jobs,id',
            'cv_id' => 'required|exists:cvs,id',
        ]);

        $userId = $request->user()->id;

        // Kiểm tra xem người dùng đã ứng tuyển chưa
        $existing = Application::where('user_id', $userId)
            ->where('job_id', $request->job_id)
            ->first();
        if ($existing) {
            return response()->json(['message' => 'Bạn đã ứng tuyển công việc này!'], 400);
        }

        // Tạo ứng tuyển
        $application = Application::create([
            'user_id' => $userId,
            'job_id' => $request->job_id,
            'cv_id' => $request->cv_id,
        ]);

        // Lấy thông tin công việc và CV
        $job = Job::find($request->job_id);
        $cv = CV::find($request->cv_id);

        // Kiểm tra xem $job có tồn tại không
        if (!$job) {
            Log::error('Job not found for job_id: ' . $request->job_id);
            return response()->json(['message' => 'Công việc không tồn tại!'], 404);
        }

        // Xử lý dữ liệu JSON
        $workExperience = $cv->work_experience;
        $workExperienceFormatted = 'Không có thông tin';
        if (is_string($workExperience) && !empty($workExperience)) {
            $workExperienceData = json_decode($workExperience, true);
            if (json_last_error() === JSON_ERROR_NONE && is_array($workExperienceData) && !empty($workExperienceData)) {
                $workExperienceFormatted = '';
                foreach ($workExperienceData as $exp) {
                    $companyName = $exp['company_name'] ?? 'Không xác định';
                    $description = $exp['description'] ?? 'Không có mô tả';
                    $endDate = $exp['end_date'] ?? 'Không xác định';
                    $workExperienceFormatted .= "- $companyName: $description (Kết thúc: $endDate)\n";
                }
            } else {
                Log::warning('Invalid JSON in work_experience for cv_id: ' . $cv->id . ', data: ' . $workExperience);
            }
        }

        $skills = $cv->skills;
        $skillsFormatted = 'Không có thông tin';
        if (is_string($skills) && !empty($skills)) {
            $skillsData = json_decode($skills, true);
            if (json_last_error() === JSON_ERROR_NONE && is_array($skillsData) && !empty($skillsData)) {
                $skillsFormatted = '';
                foreach ($skillsData as $skill) {
                    if (is_array($skill)) {
                        $name = $skill['name'] ?? 'Không xác định';
                        $description = $skill['description'] ?? 'Không có mô tả';
                        $skillsFormatted .= "- $name: $description\n";
                    } else {
                        $skillsFormatted .= "- $skill\n";
                    }
                }
            } else {
                Log::warning('Invalid JSON in skills for cv_id: ' . $cv->id . ', data: ' . $skills);
            }
        }

        $educationExperience = $cv->education_experience;
        $educationFormatted = 'Không có thông tin';
        if (is_string($educationExperience) && !empty($educationExperience)) {
            $educationData = json_decode($educationExperience, true);
            if (json_last_error() === JSON_ERROR_NONE && is_array($educationData) && !empty($educationData)) {
                $educationFormatted = '';
                foreach ($educationData as $edu) {
                    $schoolName = $edu['school_name'] ?? 'Không xác định';
                    $major = $edu['major'] ?? 'Không xác định';
                    $endDate = $edu['end_date'] ?? 'Không xác định';
                    $educationFormatted .= "- $schoolName (Chuyên ngành: $major, Kết thúc: $endDate)\n";
                }
            } else {
                Log::warning('Invalid JSON in education_experience for cv_id: ' . $cv->id . ', data: ' . $educationExperience);
            }
        }

        // Tạo file PDF từ thông tin CV
        $pdf = Pdf::loadView('pdfs.cv', [
            'fullName' => $cv->full_name,
            'jobTitle' => $cv->job_title,
            'highestEducation' => $cv->highest_education,
            'experience' => $cv->experience,
            'phone' => $cv->phone ?? 'Không có thông tin',
            'email' => $cv->email,
            'workArea' => $cv->work_area,
            'address' => $cv->address ?? 'Không có thông tin',
            'gender' => $cv->gender,
            'dateOfBirth' => $cv->date_of_birth,
            'salary' => $cv->salary,
            'linkedin' => $cv->linkedin ?? 'Không có thông tin',
            'objective' => $cv->objective ?? 'Không có thông tin',
            'workExperience' => $workExperienceFormatted,
            'skills' => $skillsFormatted,
            'educationExperience' => $educationFormatted,
            'hobbies' => $cv->hobbies ?? 'Không có thông tin',
            'additionalInfo' => $cv->additional_info ?? 'Không có thông tin',
        ]);

        // Đảm bảo thư mục tồn tại trước khi lưu file PDF
        $pdfDir = storage_path('app/public/cvs');
        if (!File::exists($pdfDir)) {
            File::makeDirectory($pdfDir, 0755, true);
        }

        // Lưu file PDF tạm vào storage
        $pdfPath = storage_path('app/public/cvs/cv_' . $userId . '_' . time() . '.pdf');
        $pdf->save($pdfPath);

        // Gửi email đến nhà tuyển dụng qua Gmail
        try {
            if (empty($job->contact_email)) {
                Log::warning('Contact email of employer is empty for job_id: ' . $job->id);
            } else {
                Mail::to($job->contact_email)->send(new ApplicationNotification($application, $job, $cv, $pdfPath));
            }
        } catch (\Exception $e) {
            Log::error('Failed to send email to employer: ' . $e->getMessage());
        }

        // Xóa file PDF tạm sau khi gửi
        if (file_exists($pdfPath)) {
            unlink($pdfPath);
        }

        return response()->json(['message' => 'Ứng tuyển thành công!', 'data' => $application], 201);
    }

    public function index()
    {
        $applications = Application::with(['user', 'job', 'cv'])->get();
        return view('admin.applications.index', compact('applications'));
    }
    public function getUserApplications(Request $request)
{
    $userId = $request->user()->id;

    $applications = Application::where('user_id', $userId)
        ->with(['job', 'cv'])
        ->get();

    return response()->json($applications);
}
}