<?php

namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\DB;
use App\Models\User;

class ResetPasswordController extends Controller
{
    public function reset(Request $request)
    {
        // Validate input
        $request->validate([
            'email' => 'required|email',
            'token' => 'required|string',
            'password' => 'required|string|min:6|confirmed',
        ]);

        // Tìm token trong bảng password_reset_tokens
        $passwordReset = DB::table('password_reset_tokens')
            ->where('email', $request->email)
            ->first();

        if (!$passwordReset) {
            return response()->json(['message' => 'Token không hợp lệ hoặc đã hết hạn'], 400);
        }

        // Kiểm tra token
        if (!Hash::check($request->token, $passwordReset->token)) {
            return response()->json(['message' => 'Mã OTP không đúng'], 400);
        }

        // Kiểm tra thời hạn token (60 phút)
        if (now()->subMinutes(60)->isAfter($passwordReset->created_at)) {
            DB::table('password_reset_tokens')
                ->where('email', $request->email)
                ->delete();
            return response()->json(['message' => 'Mã OTP đã hết hạn'], 400);
        }

        // Cập nhật mật khẩu
        $user = User::where('email', $request->email)->first();
        if (!$user) {
            return response()->json(['message' => 'Người dùng không tồn tại'], 404);
        }

        $user->password = Hash::make($request->password);
        $user->save();

        // Xóa token sau khi reset thành công
        DB::table('password_reset_tokens')
            ->where('email', $request->email)
            ->delete();

        return response()->json(['message' => 'Mật khẩu đã được đặt lại thành công'], 200);
    }
}