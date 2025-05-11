<?php
namespace App\Mail;

use App\Models\Application;
use App\Models\Job;
use App\Models\CV;
use Illuminate\Bus\Queueable;
use Illuminate\Mail\Mailable;
use Illuminate\Queue\SerializesModels;

class ApplicationNotification extends Mailable
{
    use Queueable, SerializesModels;

    public $application;
    public $job;
    public $cv;
    public $pdfPath;

    public function __construct(Application $application, Job $job, CV $cv, $pdfPath)
    {
        $this->application = $application;
        $this->job = $job;
        $this->cv = $cv;
        $this->pdfPath = $pdfPath;
    }

    public function build()
    {
        return $this->subject('Có ứng viên mới ứng tuyển công việc: ' . $this->job->title)
                    ->view('emails.application_notification')
                    ->with([
                        'jobTitle' => $this->job->title,
                        'applicantName' => $this->application->user->name,
                        'cvDetails' => $this->cv->job_title,
                        'cv' => $this->cv, // Truyền toàn bộ đối tượng CV để dùng trong view
                    ])
                    ->attach($this->pdfPath, [
                        'as' => 'CV_' . $this->application->user->name . '.pdf',
                        'mime' => 'application/pdf',
                    ]);
    }
}