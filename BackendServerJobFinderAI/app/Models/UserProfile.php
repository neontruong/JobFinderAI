<?php
namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class UserProfile extends Model
{
    protected $fillable = [
        'user_id', 'avatar', 'full_name', 'job_title', 'email', 'phone', 'skills', 'location', 'salary',
        'experience', 'preference', 'education', 'industry'

    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}