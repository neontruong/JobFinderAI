<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Job;
use Illuminate\Support\Facades\Storage;
use Intervention\Image\ImageManager;
use Intervention\Image\Drivers\Gd\Driver; // Thêm import cho driver

class AdminController extends Controller
{
    // Hiển thị danh sách jobs
    public function index(Request $request)
    {
        $search = $request->query('search');
        $query = Job::query();

        if ($search) {
            $query->where('title', 'like', "%{$search}%")
                  ->orWhere('company', 'like', "%{$search}%")
                  ->orWhere('contact_phone', 'like', "%{$search}%")
                  ->orWhere('contact_email', 'like', "%{$search}%");
        }

        $jobs = $query->paginate(10);
        return view('admin.dashboard', compact('jobs', 'search'));
    }

    // Hiển thị form tạo job mới
    public function create()
    {
        return view('admin.jobs.create');
    }

    // Lưu job mới
    public function store(Request $request)
    {
        $request->validate([
            'title' => 'required|string|max:255',
            'description' => 'required|string',
            'company' => 'required|string|max:255',
            'location' => 'required|string|max:255',
            'salary' => 'required|numeric|min:0',
            'company_description' => 'nullable|string',
            'contact_phone' => 'nullable|string|max:20',
            'contact_email' => 'nullable|email|max:255',
            'company_logo' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
        ]);

        $data = [
            'title' => $request->title,
            'description' => $request->description,
            'company' => $request->company,
            'location' => $request->location,
            'salary' => $request->salary,
            'company_description' => $request->company_description,
            'contact_phone' => $request->contact_phone,
            'contact_email' => $request->contact_email,
        ];

        // Xử lý upload và resize logo
        if ($request->hasFile('company_logo')) {
            $logo = $request->file('company_logo');
            $logoName = time() . '_' . $logo->getClientOriginalName();
            $logoPath = 'logos/' . $logoName;

            // Dòng 68: Sửa cách khởi tạo ImageManager
            $manager = new ImageManager(new Driver());

            $image = $manager->read($logo)->resize(200, 200, function ($constraint) {
                $constraint->aspectRatio();
                $constraint->upsize();
            });

            // Lưu logo đã resize vào storage
            Storage::disk('public')->put($logoPath, (string) $image->encode());

            $data['company_logo'] = $logoPath;
        }

        Job::create($data);
        return redirect()->route('admin.dashboard')->with('success', 'Thêm công việc thành công!');
    }

    // Hiển thị form chỉnh sửa job
    public function edit($id)
    {
        $job = Job::findOrFail($id);
        return view('admin.jobs.edit', compact('job'));
    }

    // Cập nhật job
    public function update(Request $request, $id)
    {
        $request->validate([
            'title' => 'required|string|max:255',
            'description' => 'required|string',
            'company' => 'required|string|max:255',
            'location' => 'required|string|max:255',
            'salary' => 'required|numeric|min:0',
            'company_description' => 'nullable|string',
            'contact_phone' => 'nullable|string|max:20',
            'contact_email' => 'nullable|email|max:255',
            'company_logo' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
        ]);

        $job = Job::findOrFail($id);

        $data = [
            'title' => $request->title,
            'description' => $request->description,
            'company' => $request->company,
            'location' => $request->location,
            'salary' => $request->salary,
            'company_description' => $request->company_description,
            'contact_phone' => $request->contact_phone,
            'contact_email' => $request->contact_email,
        ];

        // Xử lý upload và resize logo
        if ($request->hasFile('company_logo')) {
            // Xóa logo cũ nếu có
            if ($job->company_logo) {
                Storage::disk('public')->delete($job->company_logo);
            }

            $logo = $request->file('company_logo');
            $logoName = time() . '_' . $logo->getClientOriginalName();
            $logoPath = 'logos/' . $logoName;

            // Dòng 131: Sửa cách khởi tạo ImageManager
            $manager = new ImageManager(new Driver());

            $image = $manager->read($logo)->resize(200, 200, function ($constraint) {
                $constraint->aspectRatio();
                $constraint->upsize();
            });

            // Lưu logo đã resize vào storage
            Storage::disk('public')->put($logoPath, (string) $image->encode());

            $data['company_logo'] = $logoPath;
        }

        $job->update($data);
        return redirect()->route('admin.dashboard')->with('success', 'Cập nhật công việc thành công!');
    }

    // Xóa job
    public function destroy($id)
    {
        $job = Job::findOrFail($id);
        // Xóa logo nếu có
        if ($job->company_logo) {
            Storage::disk('public')->delete($job->company_logo);
        }
        $job->delete();
        return redirect()->route('admin.dashboard')->with('success', 'Xóa công việc thành công!');
    }
}