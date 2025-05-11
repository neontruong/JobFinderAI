<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Password;
use Illuminate\Support\Facades\Log; 
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Str;
use Illuminate\Support\Facades\DB;
use App\Models\JobListing;
use Illuminate\Support\Facades\Auth;
// Thêm dòng này
class AuthController extends Controller
{
    public function register(Request $request)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:6',
        ]);

        $user = User::create([
            'name' => $request->name,
            'email' => $request->email,
            'password' => Hash::make($request->password),
        ]);

        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'message' => 'Đăng ký thành công',
            'user' => $user,
            'token' => $token,
        ], 201);
    }

    public function login(Request $request)
{
    $request->validate([
        'email' => 'required|string|email',
        'password' => 'required|string',
    ]);

    $user = User::where('email', $request->email)->first();

    if (!$user || !Hash::check($request->password, $user->password)) {
        return response()->json(['message' => 'Email hoặc mật khẩu không đúng'], 401);
    }

    $token = $user->createToken('auth_token')->plainTextToken;

    return response()->json([
        'userId' => $user->id,
        'message' => 'Đăng nhập thành công',
        'token' => $token,
        'role' => $user->role, // Thêm role vào response
    ]);
}

public function forgotPassword(Request $request)
{
    // Validate email
    $request->validate(['email' => 'required|email']);

    // Tìm user theo email
    $user = User::where('email', $request->email)->first();
    if (!$user) {
        return response()->json(['message' => 'Email không tồn tại'], 404);
    }

    // Xóa token cũ nếu có
    DB::table('password_reset_tokens')
        ->where('email', $request->email)
        ->delete();

    // Tạo OTP 6 ký tự
    $token = strtoupper(Str::random(6));

    // Lưu token vào bảng password_reset_tokens
    DB::table('password_reset_tokens')->insert([
        'email' => $user->email,
        'token' => Hash::make($token),
        'created_at' => now(),
    ]);

    // Gửi email với OTP
    try {
        Mail::send('emails.reset-password', ['user' => $user, 'token' => $token], function ($message) use ($user) {
            $message->to($user->email)
                    ->subject('Đặt Lại Mật Khẩu');
        });
    } catch (\Exception $e) {
        Log::error('Lỗi gửi email đặt lại mật khẩu: ' . $e->getMessage());
        return response()->json(['message' => 'Lỗi khi gửi email, vui lòng thử lại sau'], 500);
    }

    Log::info('Yêu cầu đặt lại mật khẩu được gửi tới: ' . $user->email);

    return response()->json(['message' => 'Mã OTP đã được gửi đến email của bạn'], 200);
}
// public function storeJob(Request $request)
//     {
//         $request->validate([
//             'title' => 'required|string|max:255',
//             'description' => 'required|string',
//             'company' => 'required|string|max:255',
//             'location' => 'required|string|max:255',
//             'salary' => 'required|numeric',
//             'user_id' => 'required|exists:users,id',
//         ]);

//         $job = JobListing::create([
//             'title' => $request->title,
//             'description' => $request->description,
//             'company' => $request->company,
//             'location' => $request->location,
//             'salary' => $request->salary,
//             'user_id' => $request->user_id,
//         ]);

//         return response()->json([
//             'message' => 'Thêm công việc thành công',
//         ], 201);
//     }
    // Phương thức cho web login
    public function showLoginForm()
    {
        return view('auth.login');
    }

    // Phương thức xử lý đăng nhập cho web
    public function loginWeb(Request $request)
    {
        $request->validate([
            'email' => 'required|string|email',
            'password' => 'required|string',
        ]);

        $user = User::where('email', $request->email)->first();

        if (!$user || !Hash::check($request->password, $user->password)) {
            return back()->withErrors(['email' => 'Email hoặc mật khẩu không đúng']);
        }

        Auth::login($user); // Đăng nhập user vào session

        // Chuyển hướng dựa trên role
        if ($user->role === 'admin') {
            return redirect()->route('admin.dashboard');
        }

        return redirect()->route('home');
    }

    // Phương thức đăng xuất
    public function logout(Request $request)
    {
        Auth::logout();
        $request->session()->invalidate();
        $request->session()->regenerateToken();
        return redirect('/login');
    }
}