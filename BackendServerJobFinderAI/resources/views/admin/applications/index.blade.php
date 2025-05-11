@extends('layouts.app')

@section('content')
<div class="container mt-5">
    <h1>Danh sách ứng tuyển</h1>
    @if (session('success'))
        <div class="alert alert-success">
            {{ session('success') }}
        </div>
    @endif

    <table class="table table-bordered table-hover">
        <thead class="table-dark">
            <tr>
                <th>#</th>
                <th>Người ứng tuyển</th>
                <th>Công việc</th>
                <th>Vị trí ứng tuyển</th>
                <th>Ngày ứng tuyển</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            @forelse($applications as $application)
                <tr>
                    <td>{{ $application->id }}</td>
                    <td>{{ $application->user->name }}</td>
                    <td>{{ $application->job->title }}</td>
                    <td>{{ $application->cv->job_title }}</td>
                    <td>{{ $application->applied_at ? $application->applied_at->format('d/m/Y H:i') : 'N/A' }}</td>
                    <td>
                        <!-- Nút để hiển thị/ẩn chi tiết CV -->
                        <button class="btn btn-sm btn-info toggle-details" type="button" data-bs-toggle="collapse" data-bs-target="#cvDetails{{ $application->id }}" aria-expanded="false" aria-controls="cvDetails{{ $application->id }}">
                            Xem chi tiết CV
                        </button>
                    </td>
                </tr>
                <!-- Phần chi tiết CV (ẩn ban đầu) -->
                <tr class="collapse" id="cvDetails{{ $application->id }}">
                    <td colspan="6">
                        <div class="p-3">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h6 class="text-primary mb-0">Thông tin CV</h6>
                                <!-- Nút thu gọn -->
                                <button class="btn btn-sm btn-secondary collapse-details" type="button" data-bs-toggle="collapse" data-bs-target="#cvDetails{{ $application->id }}">
                                    Thu gọn
                                </button>
                            </div>
                            <p><strong>Họ và tên:</strong> {{ $application->cv->full_name }}</p>
                            <p><strong>Vị trí ứng tuyển:</strong> {{ $application->cv->job_title }}</p>
                            <p><strong>Bằng cấp cao nhất:</strong> {{ $application->cv->highest_education }}</p>
                            <p><strong>Kinh nghiệm:</strong> {{ $application->cv->experience }}</p>
                            <p><strong>Số điện thoại:</strong> {{ $application->cv->phone ?? 'N/A' }}</p>
                            <p><strong>Email:</strong> {{ $application->cv->email }}</p>
                            <p><strong>Khu vực làm việc:</strong> {{ $application->cv->work_area }}</p>
                            <p><strong>Địa chỉ:</strong> {{ $application->cv->address ?? 'N/A' }}</p>
                            <p><strong>Giới tính:</strong> {{ $application->cv->gender }}</p>
                            <p><strong>Ngày sinh:</strong> {{ $application->cv->date_of_birth }}</p>
                            <p><strong>Mức lương mong muốn:</strong> {{ $application->cv->salary }}</p>
                            <p><strong>LinkedIn:</strong> {{ $application->cv->linkedin ?? 'N/A' }}</p>
                            <p><strong>Mục tiêu nghề nghiệp:</strong> {{ $application->cv->objective ?? 'N/A' }}</p>

                            <!-- Hiển thị kinh nghiệm làm việc -->
                            <h6>Kinh nghiệm làm việc</h6>
                            @if ($application->cv->work_experience)
                                @foreach ($application->cv->work_experience as $work)
                                    <div class="mb-2">
                                        <p><strong>Vị trí:</strong> {{ $work['job_title'] }}</p>
                                        <p><strong>Công ty:</strong> {{ $work['company_name'] }}</p>
                                        <p><strong>Thời gian:</strong> {{ $work['start_date'] }} - {{ $work['end_date'] }}</p>
                                        <p><strong>Mô tả:</strong> {{ $work['description'] }}</p>
                                    </div>
                                @endforeach
                            @else
                                <p>Không có kinh nghiệm làm việc.</p>
                            @endif

                            <!-- Hiển thị kỹ năng -->
                            <h6>Kỹ năng</h6>
                            @if ($application->cv->skills)
                                @foreach ($application->cv->skills as $skill)
                                    <div class="mb-2">
                                        <p><strong>Tên kỹ năng:</strong> {{ $skill['name'] }}</p>
                                        <p><strong>Mô tả:</strong> {{ $skill['description'] }}</p>
                                    </div>
                                @endforeach
                            @else
                                <p>Không có kỹ năng.</p>
                            @endif

                            <!-- Hiển thị học vấn -->
                            <h6>Học vấn</h6>
                            @if ($application->cv->education_experience)
                                @foreach ($application->cv->education_experience as $education)
                                    <div class="mb-2">
                                        <p><strong>Trường:</strong> {{ $education['school_name'] }}</p>
                                        <p><strong>Chuyên ngành:</strong> {{ $education['major'] }}</p>
                                        <p><strong>Thời gian:</strong> {{ $education['start_date'] }} - {{ $education['end_date'] }}</p>
                                        <p><strong>Mô tả:</strong> {{ $education['description'] }}</p>
                                    </div>
                                @endforeach
                            @else
                                <p>Không có thông tin học vấn.</p>
                            @endif

                            <p><strong>Sở thích:</strong> {{ $application->cv->hobbies ?? 'N/A' }}</p>
                            <p><strong>Thông tin bổ sung:</strong> {{ $application->cv->additional_info ?? 'N/A' }}</p>
                        </div>
                    </td>
                </tr>
            @empty
                <tr>
                    <td colspan="6" class="text-center">Không có ứng tuyển nào.</td>
                </tr>
            @endforelse
        </tbody>
    </table>

    @if ($applications instanceof \Illuminate\Pagination\LengthAwarePaginator)
        {{ $applications->links('pagination::bootstrap-5') }}
    @endif
</div>

<style>
    /* Tùy chỉnh giao diện nhỏ gọn hơn */
    .table th, .table td {
        font-size: 14px;
        padding: 8px;
    }
    .table th {
        background-color: #343a40;
        color: white;
    }
    .collapse .p-3 {
        font-size: 14px;
    }
    .collapse h6 {
        font-size: 16px;
        margin-top: 10px;
    }
    .btn-sm {
        font-size: 12px;
        padding: 4px 8px;
    }
</style>

<script>
    // JavaScript để thay đổi văn bản của nút "Xem chi tiết CV" khi hiển thị/ẩn
    document.addEventListener('DOMContentLoaded', function () {
        const toggleButtons = document.querySelectorAll('.toggle-details');
        toggleButtons.forEach(button => {
            button.addEventListener('click', function () {
                const isExpanded = this.getAttribute('aria-expanded') === 'true';
                this.textContent = isExpanded ? 'Xem chi tiết CV' : 'Ẩn chi tiết CV';
            });
        });
    });
</script>
@endsection