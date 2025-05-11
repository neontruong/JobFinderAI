<?php
namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class CV extends Model
{
    protected $table = 'cvs'; // Chỉ định rõ tên bảng
    protected $fillable = [
        'user_id', 'avatar', 'full_name', 'job_title', 'highest_education', 'experience',
        'phone', 'email', 'work_area', 'address', 'gender', 'date_of_birth',
        'salary', 'linkedin', 'objective', 'work_experience', 'skills', 'education_experience',
        'hobbies', 'additional_info'
    ];

    protected $casts = [
        'work_experience' => 'array',
        'skills' => 'array',
        'education_experience' => 'array',
        'date_of_birth' => 'date',
    ];

    public function user()
    {
        return $this->belongsTo(User::class, 'user_id', 'id');
    }
}