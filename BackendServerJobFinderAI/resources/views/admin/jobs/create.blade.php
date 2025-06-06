<!DOCTYPE html>
<html>
<head>
    <title>Thêm công việc mới</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .form-label {
            font-weight: bold;
        }
        .form-control, .form-control-file {
            border-radius: 5px;
        }
        .btn {
            border-radius: 5px;
        }
    </style>
</head>
<body>
    <div class="container mt-5">
        <h1>Thêm công việc mới</h1>
        <!-- Thêm enctype="multipart/form-data" để hỗ trợ upload file -->
        <form method="POST" action="{{ route('admin.jobs.store') }}" enctype="multipart/form-data">
            @csrf
            <div class="mb-3">
                <label class="form-label">Tiêu đề <span class="text-danger">*</span></label>
                <input type="text" name="title" class="form-control" value="{{ old('title') }}">
                @error('title')
                    <div class="text-danger">{{ $message }}</div>
                @enderror
            </div>
            <div class="mb-3">
                <label class="form-label">Mô tả <span class="text-danger">*</span></label>
                <textarea name="description" class="form-control">{{ old('description') }}</textarea>
                @error('description')
                    <div class="text-danger">{{ $message }}</div>
                @enderror
            </div>
            <div class="mb-3">
                <label class="form-label">Công ty <span class="text-danger">*</span></label>
                <input type="text" name="company" class="form-control" value="{{ old('company') }}">
                @error('company')
                    <div class="text-danger">{{ $message }}</div>
                @enderror
            </div>
            <div class="mb-3">
                <label class="form-label">Địa điểm <span class="text-danger">*</span></label>
                <input type="text" name="location" class="form-control" value="{{ old('location') }}">
                @error('location')
                    <div class="text-danger">{{ $message }}</div>
                @enderror
            </div>
            <div class="mb-3">
                <label class="form-label">Lương <span class="text-danger">*</span></label>
                <input type="number" name="salary" class="form-control" value="{{ old('salary') }}">
                @error('salary')
                    <div class="text-danger">{{ $message }}</div>
                @enderror
            </div>
            <div class="mb-3">
                <label class="form-label">Mô tả công ty</label>
                <textarea name="company_description" class="form-control">{{ old('company_description') }}</textarea>
                @error('company_description')
                    <div class="text-danger">{{ $message }}</div>
                @enderror
            </div>
            <div class="mb-3">
                <label class="form-label">Số điện thoại liên hệ</label>
                <input type="text" name="contact_phone" class="form-control" value="{{ old('contact_phone') }}">
                @error('contact_phone')
                    <div class="text-danger">{{ $message }}</div>
                @enderror
            </div>
            <div class="mb-3">
                <label class="form-label">Email liên hệ</label>
                <input type="email" name="contact_email" class="form-control" value="{{ old('contact_email') }}">
                @error('contact_email')
                    <div class="text-danger">{{ $message }}</div>
                @enderror
            </div>
            <!-- Thêm trường upload logo -->
            <div class="mb-3">
                <label class="form-label">Logo công ty (tùy chọn)</label>
                <input type="file" name="company_logo" class="form-control" accept="image/jpeg,image/png,image/jpg,image/gif">
                <small class="form-text text-muted">Định dạng: JPEG, PNG, JPG, GIF. Dung lượng tối đa: 2MB.</small>
                @error('company_logo')
                    <div class="text-danger">{{ $message }}</div>
                @enderror
            </div>
            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-success">Thêm</button>
                <a href="{{ route('admin.dashboard') }}" class="btn btn-secondary">Quay lại</a>
            </div>
        </form>
    </div>
</body>
</html>