<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Auth\Middleware\Authenticate as Middleware;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;

class AuthenticateApi extends Middleware {
    public function handle($request, Closure $next, ...$guards) {
        Log::info('AuthenticateApi: Handling request', [
            'path' => $request->path(),
            'headers' => $request->headers->all(),
        ]);

        try {
            $this->authenticate($request, $guards);
        } catch (\Illuminate\Auth\AuthenticationException $e) {
            Log::warning('Authentication failed', ['error' => $e->getMessage()]);
            return response()->json(['error' => 'Unauthenticated'], 401);
        }

        return $next($request);
    }

    protected function redirectTo($request)
{
    if (!$request->expectsJson()) {
        return route('/api/login'); // Đường dẫn phù hợp với API của bạn
    }
        return null;
    }
}
