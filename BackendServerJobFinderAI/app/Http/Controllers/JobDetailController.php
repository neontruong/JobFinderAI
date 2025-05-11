<?php
namespace App\Http\Controllers;

use App\Models\Job;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;

class JobDetailController extends Controller
{
    public function show($id)
    {
        // Thêm caching (tùy chọn)
        $job = Cache::remember("job_{$id}", 60, function () use ($id) {
            return Job::select(
                'id',
                'title',
                'company',
                'location',
                'salary',
                'company_logo',
                'description',
                'contact_phone',
                'contact_email',
                'company_description',
                'created_at'
            )->find($id);
        });

        if (!$job) {
            return response()->json(['message' => 'Job not found'], 404);
        }

        return response()->json([
            'id' => $job->id,
            'title' => $job->title,
            'company' => $job->company,
            'location' => $job->location,
            'salary' => (float) $job->salary, // Ép kiểu salary thành float
            'logo_url' => $job->company_logo ? asset('storage/' . $job->company_logo) : null,
            'description' => $job->description,
            'contact_phone' => $job->contact_phone,
            'contact_email' => $job->contact_email,
            'company_description' => $job->company_description,
            'posted_at' => $job->created_at->toDateTimeString(),
            // Chuẩn bị cho tương lai
            'requirements' => $job->requirements ?? null,
            'benefits' => $job->benefits ?? null,
        ]);
    }
}