<?php
namespace App\Http\Controllers;

use App\Models\UserProfile;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Log;
use Illuminate\Validation\ValidationException;

class UserProfileController extends Controller
{
    public function __construct()
    {
        // Đảm bảo tất cả response đều là JSON
        $this->middleware('api');
    }

    public function saveProfile(Request $request)
    {
        try {
            Log::info('User ID received for save: ' . $request->user_id);
            Log::info('Request data: ', $request->all());

            $validated = $request->validate([
                'user_id' => 'required|exists:users,id|unique:user_profiles,user_id',
                'avatar' => 'nullable|image|max:2048',
                'full_name' => 'required|string|max:255',
                'job_title' => 'nullable|string|max:255',
                'email' => 'required|email|unique:user_profiles,email',
                'phone' => 'nullable|string|max:255',
                'skills' => 'nullable|string',
                'experience' => 'nullable|string',
                'preference' => 'nullable|string',
                'location' => 'nullable|string|max:255',
                'salary' => 'nullable|numeric',
                'education' => 'nullable|string',
                'industry' => 'nullable|string'
            ]);

            $avatarPath = null;
            if ($request->hasFile('avatar')) {
                $avatarPath = $request->file('avatar')->store('avatars', 'public');
            }

            $profile = UserProfile::create([
                'user_id' => $request->user_id,
                'avatar' => $avatarPath,
                'full_name' => $request->full_name,
                'job_title' => $request->job_title,
                'email' => $request->email,
                'phone' => $request->phone,
                'skills' => $request->skills,
                'experience' => $request->experience,
                'preference' => $request->preference,
                'location' => $request->location,
                'salary' => $request->salary,
                'education' => $request->education,
                'industry' => $request->industry
            ]);

            // Đánh dấu profile đã hoàn thành
            $user = User::find($request->user_id);
            $user->is_profile_completed = true;
            $user->save();

            return response()->json([
                'message' => 'Profile saved successfully',
                'profile' => $profile
            ], 201);
        } catch (ValidationException $e) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $e->errors()
            ], 422);
        } catch (\Exception $e) {
            Log::error('Error saving profile: ' . $e->getMessage());
            return response()->json([
                'message' => 'Error saving profile: ' . $e->getMessage()
            ], 500);
        }
    }

    public function checkProfile($userId)
    {
        try {
            $user = User::find($userId);
            if (!$user) {
                return response()->json(['error' => 'User not found'], 404);
            }
            return response()->json([
                'is_profile_completed' => $user->is_profile_completed
            ]);
        } catch (\Exception $e) {
            Log::error('Error checking profile: ' . $e->getMessage());
            return response()->json([
                'message' => 'Error checking profile: ' . $e->getMessage()
            ], 500);
        }
    }

    public function getProfile($userId)
    {
        try {
            $profile = UserProfile::where('user_id', $userId)->first();
            if (!$profile) {
                return response()->json(['message' => 'Profile not found'], 404);
            }

            return response()->json([
                'userId' => $profile->user_id,
                'avatar' => $profile->avatar ? asset('storage/' . $profile->avatar) : null,
                'full_name' => $profile->full_name,
                'job_title' => $profile->job_title,
                'email' => $profile->email,
                'phone' => $profile->phone,
                'skills' => $profile->skills,
                'experience' => $profile->experience ?? 'N/A',
                'preference' => $profile->preference ?? 'N/A',
                'location' => $profile->location,
                'salary' => $profile->salary,
                'education' => $profile->education ?? 'N/A',
                'industry' => $profile->industry ?? 'N/A'
            ]);
        } catch (\Exception $e) {
            Log::error('Error getting profile: ' . $e->getMessage());
            return response()->json([
                'message' => 'Error getting profile: ' . $e->getMessage()
            ], 500);
        }
    }

    public function updateProfile(Request $request)
{
    try {
        Log::info('User ID received for update: ' . $request->user_id);
        Log::info('Request data: ', $request->all());

        $validated = $request->validate([
            'user_id' => 'required|exists:users,id',
            'avatar' => 'nullable|image|max:2048',
            'full_name' => 'required|string|max:255',
            'job_title' => 'nullable|string|max:255',
            'email' => 'required|email|unique:user_profiles,email,' . $request->user_id . ',user_id',
            'phone' => 'nullable|string|max:255',
            'skills' => 'nullable|string',
            'experience' => 'nullable|string',
            'preference' => 'nullable|string',
            'location' => 'nullable|string|max:255',
            'salary' => 'nullable|numeric',
            'education' => 'nullable|string',
            'industry' => 'nullable|string'
        ]);

        $profile = UserProfile::where('user_id', $request->user_id)->first();
        if (!$profile) {
            return response()->json(['message' => 'Profile not found'], 404);
        }

        $avatarPath = $profile->avatar;
        if ($request->hasFile('avatar')) {
            // Kiểm tra quyền thư mục storage
            $storagePath = storage_path('app/public/avatars');
            if (!is_dir($storagePath)) {
                mkdir($storagePath, 0755, true);
            }
            if (!is_writable($storagePath)) {
                return response()->json(['message' => 'Storage directory is not writable'], 500);
            }

            if ($avatarPath && Storage::disk('public')->exists($avatarPath)) {
                Storage::disk('public')->delete($avatarPath);
            }
            $avatarPath = $request->file('avatar')->store('avatars', 'public');
        }

        $profile->update([
            'avatar' => $avatarPath,
            'full_name' => $request->full_name,
            'job_title' => $request->job_title,
            'email' => $request->email,
            'phone' => $request->phone,
            'skills' => $request->skills,
            'experience' => $request->experience,
            'preference' => $request->preference,
            'location' => $request->location,
            'salary' => $request->salary,
            'education' => $request->education,
            'industry' => $request->industry
        ]);

        return response()->json(['message' => 'Profile updated successfully']);
    } catch (ValidationException $e) {
        return response()->json([
            'message' => 'Validation failed',
            'errors' => $e->errors()
        ], 422);
    } catch (\Exception $e) {
        Log::error('Error updating profile: ' . $e->getMessage());
        return response()->json([
            'message' => 'Error updating profile: ' . $e->getMessage()
        ], 500);
    }
}
}