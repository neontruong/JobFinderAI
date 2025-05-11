<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\AdminController;
use App\Http\Controllers\ApplicationController;
use Illuminate\Support\Facades\Route;
use Illuminate\Support\Facades\Auth; // Import đúng namespace của Auth
use App\Http\Controllers\PaymentController;
// Route mặc định
Route::get('/', function () {
    return view('welcome');
});

// Override các route đăng nhập/đăng xuất
Route::get('login', [AuthController::class, 'showLoginForm'])->name('login');
Route::post('login', [AuthController::class, 'loginWeb']);
Route::post('logout', [AuthController::class, 'logout'])->name('logout');

// Route cho trang home
Route::get('/home', [App\Http\Controllers\HomeController::class, 'index'])->name('home');

// Route cho trang admin (yêu cầu đăng nhập và quyền admin)
Route::middleware(['auth', 'admin'])->group(function () {
    Route::get('/admin', [AdminController::class, 'index'])->name('admin.dashboard');
    Route::get('/admin/jobs/create', [AdminController::class, 'create'])->name('admin.jobs.create');
    Route::post('/admin/jobs/store', [AdminController::class, 'store'])->name('admin.jobs.store');
    Route::get('/admin/jobs/edit/{id}', [AdminController::class, 'edit'])->name('admin.jobs.edit');
    Route::put('/admin/jobs/update/{id}', [AdminController::class, 'update'])->name('admin.jobs.update');
    Route::delete('/admin/jobs/delete/{id}', [AdminController::class, 'destroy'])->name('admin.jobs.delete');
});
Route::middleware(['auth', 'admin'])->group(function () {
    Route::get('/admin/applications', [ApplicationController::class, 'index'])->name('admin.applications.index');
});

Route::post('/create-zalopay-order', [PaymentController::class, 'createZaloPayOrder']);
Route::post('/zalopay-callback', [PaymentController::class, 'zaloPayCallback']);

Auth::routes(['login' => false, 'logout' => false]); // Tắt route login và logout mặc định