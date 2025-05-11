<?php

namespace App\Http\Controllers;

use App\Models\Job;
use Illuminate\Http\Request;

class JobController extends Controller
{
    public function store(Request $request)
    {
        $request->validate([
            
            'title' => 'required|string',
            'description' => 'nullable|string',
            'company' => 'required|string',
            'location' => 'required|string',
            'salary' => 'required|numeric',
        ]);

        $job = Job::create($request->all());

        return response()->json(['message' => 'Job created successfully', 'data' => $job], 201);
    }

    public function index(Request $request)
    {
        $keyword = $request->query('keyword');
        $query = Job::select('id', 'title', 'company', 'location', 'salary', 'company_logo'); // Thêm company_logo vào select

        if ($keyword) {
            $query->where('title', 'like', "%{$keyword}%")
                  ->orWhere('company', 'like', "%{$keyword}%")
                  ->orWhere('location', 'like', "%{$keyword}%");
        }

        $jobs = $query->get()->map(function ($job) {
            return [
                'id' => $job->id, // Thêm id
                'title' => $job->title,
                'company' => $job->company,
                'location' => $job->location,
                'salary' => $job->salary,
                'logo_url' => $job->company_logo
                    ? asset('storage/' . $job->company_logo)
                    : null, // Hoặc trả về URL mặc định nếu cần
            ];
        });

        return response()->json($jobs);
    }
}