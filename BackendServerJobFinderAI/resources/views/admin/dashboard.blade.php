@extends('layouts.app')

@section('content')
<div class="container mt-5">
    <h1>Chào mừng đến với Trang Quản Trị</h1>
    <div class="d-flex justify-content-between mb-3">
        <a href="{{ route('logout') }}" class="btn btn-danger">Đăng xuất</a>
        <div>
            <a href="{{ route('admin.jobs.create') }}" class="btn btn-success">Thêm công việc mới</a>
            <!-- Thêm liên kết đến trang danh sách ứng tuyển -->
            <a href="{{ route('admin.applications.index') }}" class="btn btn-primary">Xem danh sách ứng tuyển</a>
        </div>
    </div>

    <!-- Form tìm kiếm -->
    <form method="GET" action="{{ route('admin.dashboard') }}" class="mb-3">
        <div class="input-group">
            <input type="text" name="search" class="form-control" placeholder="Tìm kiếm theo tiêu đề hoặc công ty" value="{{ $search ?? '' }}">
            <button type="submit" class="btn btn-primary">Tìm kiếm</button>
        </div>
    </form>

    <h3>Danh sách công việc</h3>
    @if (session('success'))
        <div class="alert alert-success">
            {{ session('success') }}
        </div>
    @endif

    <table class="table table-bordered">
        <thead>
            <tr>
                <th>ID</th>
                <th>Logo</th>
                <th>Tiêu đề</th>
                <th>Mô tả</th>
                <th>Công ty</th>
                <th>Địa điểm</th>
                <th>Lương</th>
                <th>Mô tả công ty</th>
                <th>Phone</th>
                <th>Email</th>
                <th>Ngày tạo</th>
                <th>Ngày cập nhật</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            @forelse($jobs as $job)
            <tr>
                <td>{{ $job->id }}</td>
                <td>
                    @if ($job->company_logo)
                        <img src="{{ asset('storage/' . $job->company_logo) }}" alt="{{ $job->company }}" style="max-width: 50px; max-height: 50px;">
                    @else
                        <img src="{{ asset('images/default-logo.png') }}" alt="Default Logo" style="max-width: 50px; max-height: 50px;">
                    @endif
                </td>
                <td>{{ $job->title }}</td>
                <td>{{ $job->description }}</td>
                <td>{{ $job->company }}</td>
                <td>{{ $job->location }}</td>
                <td>{{ number_format($job->salary, 0, ',', '.') }}</td>
                <td>{{ $job->company_description ?? 'N/A' }}</td>
                <td>{{ $job->contact_phone ?? 'N/A' }}</td>
                <td>{{ $job->contact_email ?? 'N/A' }}</td>
                <td>{{ $job->created_at }}</td>
                <td>{{ $job->updated_at }}</td>
                <td>
                    <a href="{{ route('admin.jobs.edit', $job->id) }}" class="btn btn-sm btn-warning">Sửa</a>
                    <form action="{{ route('admin.jobs.delete', $job->id) }}" method="POST" style="display:inline;">
                        @csrf
                        @method('DELETE')
                        <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc muốn xóa?')">Xóa</button>
                    </form>
                </td>
            </tr>
            @empty
            <tr>
                <td colspan="13" class="text-center">Không có dữ liệu</td>
            </tr>
            @endforelse
        </tbody>
    </table>

    <!-- Phân trang -->
    @if ($jobs instanceof \Illuminate\Pagination\LengthAwarePaginator)
        {{ $jobs->links('pagination::bootstrap-5') }}
    @endif
</div>
@endsection