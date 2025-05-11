<?php

return [
    'show_warnings' => false,   // Throw an Exception on warnings from dompdf

    'public_path' => null,  // Override the public path if needed

    'convert_entities' => true, // Dejavu Sans font is missing glyphs for converted entities, turn it off if you need to show € and £.

    'options' => [
        'font_dir' => storage_path('fonts'), // Thư mục lưu font tùy chỉnh
        'font_cache' => storage_path('fonts'), // Thư mục cache font
        'temp_dir' => sys_get_temp_dir(),
        'chroot' => realpath(base_path()),
        'allowed_protocols' => [
            'data://' => ['rules' => []],
            'file://' => ['rules' => []],
            'http://' => ['rules' => []],
            'https://' => ['rules' => []],
        ],
        'artifactPathValidation' => null,
        'log_output_file' => null,
        'enable_font_subsetting' => true, // Bật subsetting font để giảm kích thước file PDF
        'pdf_backend' => 'CPDF',
        'default_media_type' => 'screen',
        'default_paper_size' => 'a4',
        'default_paper_orientation' => 'portrait',
        'default_font' => 'DejaVu Sans', // Sử dụng font DejaVu Sans hỗ trợ Unicode
        'dpi' => 96,
        'enable_php' => false,
        'enable_javascript' => true,
        'enable_remote' => true, // Bật để tải font từ bên ngoài nếu cần
        'allowed_remote_hosts' => null,
        'font_height_ratio' => 1.1,
        'enable_html5_parser' => true,
    ],
];