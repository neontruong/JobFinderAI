<?php
namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Job extends Model
{
    protected $fillable = [
        'title', 'description', 'company', 'location', 'salary','company_description',
        'contact_phone', // Thêm cột mới
        'contact_email',
        'company_logo',
    ];
    
}
