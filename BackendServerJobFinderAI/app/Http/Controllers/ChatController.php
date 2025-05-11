<?php

namespace App\Http\Controllers;

use App\Models\Chat;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;

class ChatController extends Controller
{
    public function store(Request $request)
    {
        try {
            $request->validate([
                'message' => 'required_without:file|string',
                'file' => 'nullable|file|max:10240', // Giới hạn 10MB
                'is_from_user' => 'boolean'
            ]);

            $message = $request->message;
            $isFile = false;
            if ($request->hasFile('file')) {
                $file = $request->file('file');
                $path = $file->store('chat_files', 'public');
                $message = $path;
                $isFile = true;
            }

            $user = $request->user();
            if (!$user) {
                Log::error('User not authenticated in ChatController::store');
                return response()->json(['error' => 'User not authenticated'], 401);
            }

            $chat = Chat::create([
                'user_id' => $user->id,
                'message' => $message,
                'is_from_user' => $request->input('is_from_user', true),
                'is_file' => $isFile,
            ]);

            return response()->json(['success' => true, 'chat' => $chat]);
        } catch (\Exception $e) {
            Log::error('Error in ChatController::store: ' . $e->getMessage());
            return response()->json(['error' => 'Internal Server Error', 'message' => $e->getMessage()], 500);
        }
    }

    public function history(Request $request)
    {
        try {
            $user = $request->user();
            if (!$user) {
                Log::error('User not authenticated in ChatController::history');
                return response()->json(['error' => 'User not authenticated'], 401);
            }

            $chats = Chat::where('user_id', $user->id)
                         ->orderBy('created_at', 'asc')
                         ->paginate(20);
            return response()->json($chats);
        } catch (\Exception $e) {
            Log::error('Error in ChatController::history: ' . $e->getMessage());
            return response()->json(['error' => 'Internal Server Error', 'message' => $e->getMessage()], 500);
        }
    }
}