<?php
namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class CheckAdmin
{
    public function handle(Request $request, Closure $next)
    {
        if (!Auth::check()) {
            return redirect('/login')->with('error', 'Vui lòng đăng nhập!');
        }

        if (Auth::user()->role !== 'admin') {
            return redirect('/home')->with('error', 'Bạn không có quyền truy cập trang admin!');
        }

        return $next($request);
    }
}