<!DOCTYPE html>
<html>
<head>
    <title>CV - {{ $fullName }}</title>
    <style>
        body {
            font-family: 'DejaVu Sans', sans-serif;
            font-size: 12px;
            line-height: 1.5;
            margin: 20px;
        }
        h1 {
            color: #333;
            font-size: 18px;
            text-align: center;
            border-bottom: 2px solid #333;
            padding-bottom: 5px;
        }
        h2 {
            color: #555;
            font-size: 14px;
            margin-top: 15px;
            border-bottom: 1px solid #ccc;
            padding-bottom: 3px;
        }
        .section {
            margin-bottom: 15px;
        }
        .label {
            font-weight: bold;
            display: inline-block;
            width: 150px;
        }
        p {
            margin: 5px 0;
        }
        .content {
            margin-left: 160px;
        }
    </style>
</head>
<body>
    <h1>CV của {{ $fullName }}</h1>

    <div class="section">
        <h2>Thông tin cá nhân</h2>
        <p><span class="label">Họ và tên:</span> <span class="content">{{ $fullName }}</span></p>
        <p><span class="label">Giới tính:</span> <span class="content">{{ $gender }}</span></p>
        <p><span class="label">Ngày sinh:</span> <span class="content">{{ $dateOfBirth }}</span></p>
        <p><span class="label">Địa chỉ:</span> <span class="content">{{ $address }}</span></p>
        <p><span class="label">Số điện thoại:</span> <span class="content">{{ $phone }}</span></p>
        <p><span class="label">Email:</span> <span class="content">{{ $email }}</span></p>
        <p><span class="label">LinkedIn:</span> <span class="content">{{ $linkedin }}</span></p>
    </div>

    <div class="section">
        <h2>Mục tiêu nghề nghiệp</h2>
        <p>{{ $objective }}</p>
    </div>

    <div class="section">
        <h2>Vị trí ứng tuyển</h2>
        <p><span class="label">Tiêu đề công việc:</span> <span class="content">{{ $jobTitle }}</span></p>
        <p><span class="label">Khu vực làm việc:</span> <span class="content">{{ $workArea }}</span></p>
        <p><span class="label">Mức lương mong muốn:</span> <span class="content">{{ $salary }}</span></p>
    </div>

    <div class="section">
        <h2>Kinh nghiệm làm việc</h2>
        <p style="white-space: pre-wrap;">{{ $workExperience }}</p>
    </div>

    <div class="section">
        <h2>Học vấn</h2>
        <p><span class="label">Trình độ cao nhất:</span> <span class="content">{{ $highestEducation }}</span></p>
        <p style="white-space: pre-wrap;">{{ $educationExperience }}</p>
    </div>

    <div class="section">
        <h2>Kỹ năng</h2>
        <p style="white-space: pre-wrap;">{{ $skills }}</p>
    </div>

    <div class="section">
        <h2>Sở thích</h2>
        <p>{{ $hobbies }}</p>
    </div>

    <div class="section">
        <h2>Thông tin bổ sung</h2>
        <p>{{ $additionalInfo }}</p>
    </div>
</body>
</html>