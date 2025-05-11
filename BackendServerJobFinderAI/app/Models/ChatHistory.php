<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class ChatHistory extends Model
{
    protected $table = 'chat_history';
    protected $fillable = ['user_id', 'message', 'is_user', 'file_uri'];

    // Định nghĩa mối quan hệ với bảng users
    public function user()
    {
        return $this->belongsTo(User::class, 'user_id', 'id');
    }
}