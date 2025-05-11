<?php

use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;
use App\Http\Middleware\CheckRole;
use App\Http\Middleware\AuthenticateApi; // Đảm bảo đã import
use App\Http\Middleware\CheckAdmin; // Import middleware của bạn
return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        web: __DIR__.'/../routes/web.php',
        api: __DIR__.'/../routes/api.php',
        commands: __DIR__.'/../routes/console.php',
        health: '/up',
    )
    ->withMiddleware(function (Middleware $middleware) {
        // Đăng ký alias cho middleware
        $middleware->alias([
            'auth.api' => \App\Http\Middleware\AuthenticateApi::class, // Thêm dòng này
            'admin' => CheckAdmin::class,
            
        ]);
        $middleware->validateCsrfTokens(except: [
            'create-zalopay-order',
            'zalopay-callback',
        ]);
        
    })
    ->withExceptions(function (Exceptions $exceptions) {
        //
    })->create();