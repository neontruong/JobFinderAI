<?php
namespace App\Http\Controllers;

use App\Models\CV;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;

class CVController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth:sanctum');
    }

    // Lấy danh sách CV của người dùng
    public function index(Request $request)
    {
        $userId = $request->user()->id;
        $cvs = CV::where('user_id', $userId)
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json([
            'data' => $cvs
        ], 200);
    }

    // Tạo CV mới
    public function store(Request $request)
    {
        $request->validate([
            'avatar' => 'nullable|string',
            'full_name' => 'required|string|max:255',
            'job_title' => 'required|string|max:255',
            'highest_education' => 'required|string',
            'experience' => 'required|string',
            'phone' => 'required|string', // Đổi từ phone_number thành phone
            'email' => 'required|email',
            'work_area' => 'required|string',
            'address' => 'required|string', // Đổi từ detailed_address thành address
            'gender' => 'required|string',
            'date_of_birth' => 'required|date',
            'salary' => 'required|string',
            'linkedin' => 'nullable|string',
            'objective' => 'nullable|string',
            'work_experience' => 'nullable|array',
            'skills' => 'nullable|array',
            'education_experience' => 'nullable|array', // Đổi từ education thành education_experience
            'hobbies' => 'nullable|string',
            'additional_info' => 'nullable|string',
        ]);

        $userId = $request->user()->id;

        $cv = CV::create([
            'user_id' => $userId,
            'avatar' => $request->avatar,
            'full_name' => $request->full_name,
            'job_title' => $request->job_title,
            'highest_education' => $request->highest_education,
            'experience' => $request->experience,
            'phone' => $request->phone, // Đổi từ phone_number thành phone
            'email' => $request->email,
            'work_area' => $request->work_area,
            'address' => $request->address, // Đổi từ detailed_address thành address
            'gender' => $request->gender,
            'date_of_birth' => $request->date_of_birth,
            'salary' => $request->salary,
            'linkedin' => $request->linkedin,
            'objective' => $request->objective,
            'work_experience' => $request->work_experience,
            'skills' => $request->skills,
            'education_experience' => $request->education_experience, // Đổi từ education thành education_experience
            'hobbies' => $request->hobbies,
            'additional_info' => $request->additional_info,
        ]);

        return response()->json([
            'message' => 'CV created successfully',
            'data' => $cv
        ], 201);
    }

    public function show($id)
{
    try {
        $userId = request()->user()->id;
        $cv = CV::with('user')
            ->where('user_id', $userId)
            ->findOrFail($id);
        return response()->json([
            'message' => 'Success',
            'data' => $cv
        ], 200);
    } catch (\Exception $e) {
        Log::error('Error fetching CV: ' . $e->getMessage());
        return response()->json([
            'message' => 'Error fetching CV',
            'error' => $e->getMessage()
        ], 404);
    }
}
}