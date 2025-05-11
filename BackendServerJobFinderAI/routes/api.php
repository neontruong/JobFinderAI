<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\Auth\ResetPasswordController;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\JobDetailController;
use App\Http\Controllers\JobController;
use App\Http\Controllers\ChatController;
use App\Http\Controllers\UserProfileController;
use App\Http\Controllers\ChatHistoryController;
use App\Http\Controllers\CVController;
use App\Http\Controllers\ApplicationController;
use App\Http\Controllers\PaymentController;
// Đăng ký và đăng nhập
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);
Route::post('/forgot-password', [AuthController::class, 'forgotPassword']);
Route::post('/reset-password', [ResetPasswordController::class, 'reset']);

Route::post('/jobs', [JobController::class, 'store']);

Route::get('/jobs', [JobController::class, 'index']);

Route::get('/jobs/{id}', [JobDetailController::class, 'show']);

Route::post('/profile/save', [UserProfileController::class, 'saveProfile']);
Route::get('/profile/check/{userId}', [UserProfileController::class, 'checkProfile']);

Route::get('profile/{userId}', [UserProfileController::class, 'getProfile']);
Route::post('profile/update', [UserProfileController::class, 'updateProfile']);


// Route cho lịch sử chat (yêu cầu xác thực)
Route::prefix('chat')->middleware('auth:sanctum')->group(function () {
    Route::get('/history', [ChatHistoryController::class, 'index']); // Lấy lịch sử chat
    Route::post('/history', [ChatHistoryController::class, 'store']); // Lưu tin nhắn
    Route::delete('/history', [ChatHistoryController::class, 'destroy']); // Xóa lịch sử chat
   // Route::get('/profile', [ChatHistoryController::class, 'getProfile']); // Thêm route để lấy profile
   Route::get('/cover-letter/{job_id}', [ChatHistoryController::class, 'getCoverLetter']);
   Route::get('/profile', [ChatHistoryController::class, 'getProfile']); // Bỏ {userId}
});

Route::prefix('cvs')->middleware('auth:sanctum')->group(function () {
    Route::get('/', [CVController::class, 'index']);
    Route::post('/', [CVController::class, 'store']);
    Route::get('/{id}', [CVController::class, 'show']); // Thêm route này
});

Route::middleware('auth:sanctum')->group(function () {
    Route::post('/applications', [ApplicationController::class, 'store']);
    Route::get('/user-applications', [ApplicationController::class, 'getUserApplications']);
});
Route::get('/recommended-jobs', [ChatHistoryController::class, 'getRecommendedJobs']); // Route mới


// Route::post('/create-zalopay-order', [PaymentController::class, 'createZaloPayOrder']);
// Route::post('/zalopay-callback', [PaymentController::class, 'zaloPayCallback']);


