<?php

namespace App\Http\Controllers;

use App\Models\ChatHistory;
use App\Models\UserProfile;
use App\Models\Job;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Cache;

class ChatHistoryController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth:sanctum');
    }

    private function normalizeString($string)
    {
        $string = mb_strtolower($string, 'UTF-8');
        $string = preg_replace('/\s+/', ' ', trim($string));
        $string = str_replace(
            ['á', 'à', 'ả', 'ã', 'ạ', 'â', 'ấ', 'ầ', 'ẩ', 'ẫ', 'ậ', 'ă', 'ắ', 'ằ', 'ẳ', 'ẵ', 'ặ', 'đ', 'é', 'è', 'ẻ', 'ẽ', 'ẹ', 'ê', 'ế', 'ề', 'ể', 'ễ', 'ệ', 'í', 'ì', 'ỉ', 'ĩ', 'ị', 'ó', 'ò', 'ỏ', 'õ', 'ọ', 'ô', 'ố', 'ồ', 'ổ', 'ỗ', 'ộ', 'ơ', 'ớ', 'ờ', 'ở', 'ỡ', 'ợ', 'ú', 'ù', 'ủ', 'ũ', 'ụ', 'ư', 'ứ', 'ừ', 'ử', 'ữ', 'ự', 'ý', 'ỳ', 'ỷ', 'ỹ', 'ỵ'],
            ['a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'd', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'i', 'i', 'i', 'i', 'i', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'y', 'y', 'y', 'y', 'y'],
            $string
        );
        return $string;
    }

    private function getRandomResponse($responses)
    {
        return $responses[array_rand($responses)];
    }

    private function mapJobsToResponse($jobs)
    {
        return $jobs->map(function ($job) {
            return [
                'id' => $job->id,
                'title' => mb_convert_encoding($job->title, 'UTF-8', 'UTF-8'),
                'company' => mb_convert_encoding($job->company, 'UTF-8', 'UTF-8'),
                'location' => mb_convert_encoding($job->location, 'UTF-8', 'UTF-8'),
                'salary' => $job->salary,
                'logo_url' => $job->logo_url,
                'description' => mb_convert_encoding($job->description, 'UTF-8', 'UTF-8'),
                'contact_phone' => $job->contact_phone,
                'contact_email' => $job->contact_email ?? 'contact@example.com',
                'company_description' => mb_convert_encoding($job->company_description, 'UTF-8', 'UTF-8'),
                'posted_at' => $job->created_at->format('Y-m-d H:i:s'),
            ];
        })->toArray();
    }

    public function store(Request $request)
    {
        try {
            $request->validate([
                'message' => 'required|string',
                'is_user' => 'required|boolean',
                'file_uri' => 'nullable|string',
            ]);

            $userId = $request->user()->id;
            Log::info('Received message: ' . $request->message . ' from user: ' . $userId);

            $chat = ChatHistory::create([
                'user_id' => $userId,
                'message' => $request->message,
                'is_user' => $request->is_user,
                'file_uri' => $request->file_uri,
            ]);
            Log::info('User message saved: ' . $chat->id);

            $normalizedMessage = $this->normalizeString($request->message);

            $cacheKey = "chat_context_{$userId}";
            $context = Cache::get($cacheKey, [
                'topic' => '',
                'last_intent' => '',
                'chat_history' => []
            ]);

            $context['chat_history'][] = ['role' => 'user', 'message' => $request->message];
            if (count($context['chat_history']) > 10) {
                $context['chat_history'] = array_slice($context['chat_history'], -10);
            }

            if ($request->is_user && preg_match('/(toi muon tim viec lam|tim viec)\s+(.+)/i', $normalizedMessage, $matches)) {
                Log::info('Triggering job search by user input for user: ' . $userId);
                $context['topic'] = 'job';
                $context['last_intent'] = 'job_search_by_input';
                $searchKeyword = trim($matches[2]);

                $mainSkill = null;
                $keywords = [];
                if (preg_match('/\(ky nang chinh: (.+)\)/i', $searchKeyword, $skillMatches)) {
                    $skillsString = trim($skillMatches[1]);
                    $keywords = array_map('trim', explode(',', $skillsString));
                    $mainSkill = $keywords[0];
                    Log::info('Using skills from message context: ' . $skillsString . ', Main skill: ' . $mainSkill);
                } else {
                    $keywords = [$searchKeyword];
                }

                Cache::put($cacheKey, $context, now()->addMinutes(30));

                $jobsQuery = Job::query();
                if (!empty($keywords)) {
                    $jobsQuery->where(function ($query) use ($keywords) {
                        foreach ($keywords as $keyword) {
                            $normalizedKeyword = $this->normalizeString($keyword);
                            $query->orWhereRaw('LOWER(title) LIKE ?', ["%{$normalizedKeyword}%"])
                                  ->orWhereRaw('LOWER(description) LIKE ?', ["%{$normalizedKeyword}%"]);
                        }
                    });
                }

                $jobs = $jobsQuery->limit(3)->get();

                if ($mainSkill) {
                    $normalizedMainSkill = $this->normalizeString($mainSkill);
                    $jobs = $jobs->filter(function ($job) use ($normalizedMainSkill) {
                        $normalizedTitle = $this->normalizeString($job->title);
                        $normalizedDescription = $this->normalizeString($job->description);
                        return stripos($normalizedTitle, $normalizedMainSkill) !== false || 
                               stripos($normalizedDescription, $normalizedMainSkill) !== false;
                    });
                }

                if ($jobs->isEmpty()) {
                    $response = "Hiện tại mình không tìm thấy công việc nào liên quan đến " . ($mainSkill ?? $searchKeyword) . ". Bạn có muốn thử tìm với từ khóa khác không?";
                } else {
                    $jobsArray = $this->mapJobsToResponse($jobs);
                    $response = json_encode([
                        'type' => 'job_list',
                        'jobs' => $jobsArray
                    ], JSON_UNESCAPED_UNICODE);
                }
                Log::info('Job search response by input: ' . $response);

                $aiChat = ChatHistory::create([
                    'user_id' => $userId,
                    'message' => $response,
                    'is_user' => false,
                    'file_uri' => null,
                ]);
                $context['chat_history'][] = ['role' => 'assistant', 'message' => $response];
                Cache::put($cacheKey, $context, now()->addMinutes(30));
                Log::info('AI response saved: ' . $aiChat->id);
            } elseif ($request->is_user && (stripos($normalizedMessage, 'toi muon tim viec') !== false || stripos($normalizedMessage, 'tim viec') !== false || stripos($normalizedMessage, 'goi y viec lam') !== false)) {
                Log::info('Triggering job suggestion logic for user: ' . $userId);
                $context['topic'] = 'job'; // Fixed syntax
                $context['last_intent'] = 'job_suggestion'; // Fixed syntax
                Cache::put($cacheKey, $context, now()->addMinutes(30));

                $profile = UserProfile::where('user_id', $userId)->first();

                if (!$profile) {
                    $response = "Mình không tìm thấy profile của bạn. Bạn cần tạo profile trước để mình có thể gợi ý công việc nhé.";
                    Log::info('Profile not found for user: ' . $userId);
                } elseif (!$profile->skills || !$profile->location) {
                    $response = "Profile của bạn chưa đầy đủ thông tin (cần có skills và location). Bạn hãy cập nhật profile để mình gợi ý công việc phù hợp nhé.";
                    Log::info('Profile incomplete for user: ' . $userId);
                } else {
                    Log::info('Profile found: ' . json_encode($profile));
                    Log::info('User skills: ' . $profile->skills);

                    $skills = array_map('trim', explode(',', $profile->skills ?? ''));
                    $mainSkill = $skills[0] ?? null;
                    Log::info('Parsed skills: ' . json_encode($skills) . ', Main skill: ' . $mainSkill);

                    $jobsQuery = Job::where('location', 'like', "%{$profile->location}%")
                                   ->where('salary', '>=', $profile->salary ?? 0);

                    if (!empty($skills)) {
                        $jobsQuery->where(function ($query) use ($skills) {
                            foreach ($skills as $skill) {
                                $normalizedSkill = $this->normalizeString($skill);
                                Log::info('Searching for skill: ' . $normalizedSkill);
                                $query->orWhereRaw('LOWER(title) LIKE ?', ["%{$normalizedSkill}%"])
                                      ->orWhereRaw('LOWER(description) LIKE ?', ["%{$normalizedSkill}%"]);
                            }
                        });
                    }

                    $jobs = $jobsQuery->limit(3)->get();
                    Log::info('Found jobs: ' . json_encode($jobs));

                    if ($mainSkill) {
                        $normalizedMainSkill = $this->normalizeString($mainSkill);
                        $filteredJobs = $jobs->filter(function ($job) use ($normalizedMainSkill) {
                            $normalizedTitle = $this->normalizeString($job->title);
                            $normalizedDescription = $this->normalizeString($job->description);
                            return stripos($normalizedTitle, $normalizedMainSkill) !== false || 
                                   stripos($normalizedDescription, $normalizedMainSkill) !== false;
                        });
                    } else {
                        $filteredJobs = $jobs;
                    }

                    if ($filteredJobs->isEmpty()) {
                        $response = "Hiện tại mình không tìm thấy công việc nào phù hợp với kỹ năng ({$profile->skills}) và địa điểm ({$profile->location}) của bạn. Bạn có muốn thử tìm với tiêu chí khác không?";
                    } else {
                        $jobsArray = $this->mapJobsToResponse($filteredJobs);
                        $response = json_encode([
                            'type' => 'job_list',
                            'jobs' => $jobsArray
                        ], JSON_UNESCAPED_UNICODE);
                    }
                    Log::info('Job suggestion response: ' . $response);
                }

                $aiChat = ChatHistory::create([
                    'user_id' => $userId,
                    'message' => $response,
                    'is_user' => false,
                    'file_uri' => null,
                ]);
                $context['chat_history'][] = ['role' => 'assistant', 'message' => $response];
                Cache::put($cacheKey, $context, now()->addMinutes(30));
                Log::info('AI response saved: ' . $aiChat->id);
            } else {
                if ($request->is_user) {
                    $response = "Mình có thể giúp bạn tìm việc hoặc trả lời các câu hỏi khác. Bạn muốn bắt đầu với gì? Ví dụ: tôi muốn tìm việc làm Python hoặc hướng dẫn tôi làm CV.";
                } else {
                    $response = "Mình đang chờ bạn hỏi thêm. Bạn cần giúp gì?";
                }

                $aiChat = ChatHistory::create([
                    'user_id' => $userId,
                    'message' => $response,
                    'is_user' => false,
                    'file_uri' => null,
                ]);
                $context['chat_history'][] = ['role' => 'assistant', 'message' => $response];
                Cache::put($cacheKey, $context, now()->addMinutes(30));
                Log::info('AI default response saved: ' . $aiChat->id);
            }

            return response()->json([
                'message' => 'Chat saved successfully',
                'data' => $chat
            ], 201);
        } catch (\Exception $e) {
            Log::error('Error in ChatHistoryController::store: ' . $e->getMessage());
            return response()->json(['error' => 'Internal Server Error'], 500);
        }
    }

    public function index(Request $request)
    {
        try {
            $userId = $request->user()->id;
            $chats = ChatHistory::where('user_id', $userId)
                                ->orderBy('created_at', 'asc')
                                ->get();
            Log::info('Chat history loaded for user: ' . $userId . ', count: ' . $chats->count());
            return response()->json([
                'data' => $chats
            ], 200);
        } catch (\Exception $e) {
            Log::error('Error in ChatHistoryController::index: ' . $e->getMessage());
            return response()->json(['error' => 'Internal Server Error'], 500);
        }
    }

    public function destroy(Request $request)
    {
        try {
            $userId = $request->user()->id;
            $cacheKey = "chat_context_{$userId}";
            Cache::forget($cacheKey);
            ChatHistory::where('user_id', $userId)->delete();
            return response()->json([
                'message' => 'Chat history deleted successfully'
            ], 200);
        } catch (\Exception $e) {
            Log::error('Error in ChatHistoryController::destroy: ' . $e->getMessage());
            return response()->json(['error' => 'Internal Server Error'], 500);
        }
    }

    public function getProfile(Request $request)
    {
        try {
            $userId = $request->user()->id;
            Log::info('Fetching profile for user: ' . $userId);
            $profile = UserProfile::where('user_id', $userId)->first();

            if (!$profile) {
                Log::info('Profile not found for user: ' . $userId);
                return response()->json([
                    'message' => 'Profile not found',
                    'data' => null
                ], 404);
            }

            $profileData = [
                'full_name' => $profile->full_name,
                'email' => $profile->email,
                'phone' => $profile->phone,
                'skills' => $profile->skills,
                'experience' => $profile->experience,
                'education' => $profile->education,
                'location' => $profile->location,
                'salary' => $profile->salary,
            ];

            Log::info('Profile loaded for user: ' . $userId . ', data: ' . json_encode($profileData));
            return response()->json([
                'message' => 'Profile loaded successfully',
                'data' => $profileData
            ], 200);
        } catch (\Exception $e) {
            Log::error('Error in ChatHistoryController::getProfile: ' . $e->getMessage());
            return response()->json(['error' => 'Internal Server Error'], 500);
        }
    }
}