<!DOCTYPE html>
<html>
<head>
    <title>Có ứng viên mới</title>
</head>
<body>
    <h1>Có ứng viên mới ứng tuyển</h1>
    <p>Xin chào,</p>
    <p>Một ứng viên mới đã ứng tuyển vào công việc <strong>{{ $jobTitle }}</strong>.</p>
    <p><strong>Tên ứng viên:</strong> {{ $applicantName }}</p>
    <p><strong>Tiêu đề CV:</strong> {{ $cvDetails }}</p>
    <p><strong>Email ứng viên:</strong> {{ $cv->email }}</p>
    <p><strong>Số điện thoại:</strong> {{ $cv->phone ?? 'Không có thông tin' }}</p>
    <p>Vui lòng xem file CV đính kèm để biết thêm chi tiết.</p>
    <p>Trân trọng,<br>SmartJobAI Team</p>
</body>
</html>