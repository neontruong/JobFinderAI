<!DOCTYPE html>
<html>
<head>
    <title>Đặt Lại Mật Khẩu</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background: #f8f8f8; padding: 10px; text-align: center; }
        .content { padding: 20px; }
        .otp { font-size: 24px; font-weight: bold; color: #007bff; }
        .footer { font-size: 12px; color: #777; text-align: center; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>{{ config('app.name') }}</h2>
        </div>
        <div class="content">
            <p>Xin chào {{ $user->name }},</p>
            <p>Bạn đã yêu cầu đặt lại mật khẩu. Dưới đây là mã OTP của bạn:</p>
            <p class="otp">{{ $token }}</p>
            <p>Mã này có hiệu lực trong <strong>60 phút</strong>. Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>
            <p>Trân trọng,</p>
            <p>{{ config('app.name') }}</p>
        </div>
        <div class="footer">
            <p>Email này được gửi tự động, vui lòng không trả lời.</p>
        </div>
    </div>
</body>
</html>