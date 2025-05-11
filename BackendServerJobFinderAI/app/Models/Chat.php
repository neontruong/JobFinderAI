<?php

// app/Models/Chat.php
namespace App\Models;
use Illuminate\Database\Eloquent\Factories\HasFactory;

use Illuminate\Database\Eloquent\Model;

class Chat extends Model
{
    use HasFactory;
    protected $table = 'chat_message'; // Liên kết với bảng chat_message
    protected $fillable = ['user_id', 'message', 'is_from_user']; // Các cột có thể điền dữ liệu

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}