<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Log;

class PaymentController extends Controller
{
    // Bỏ qua middleware CSRF
    public function __construct()
    {
        $this->middleware('api')->only(['createZaloPayOrder', 'zaloPayCallback']);
    }

    public function createZaloPayOrder(Request $request)
{
    $appId = env('ZALOPAY_APP_ID');
    $key1 = env('ZALOPAY_KEY1');
    $createOrderUrl = env('ZALOPAY_CREATE_ORDER_URL');

    // Tạo mã giao dịch duy nhất (yymmdd_xxxxxx)
    $randomNum = str_pad(mt_rand(0, 999999), 6, '0', STR_PAD_LEFT); // Tạo số ngẫu nhiên 6 chữ số
    $appTransId = date('ymd') . '_' . $randomNum; // Ví dụ: 250401_123456
    $amount = $request->input('amount', 10000); // Mặc định 10,000 VND
    $description = $request->input('description', 'Thanh toán để mở khóa lượt apply thêm');
    $callbackUrl = env('ZALOPAY_CALLBACK_URL');

    // Tạo dữ liệu gửi đến ZaloPay
    $params = [
        'appid' => $appId,
        'apptransid' => $appTransId,
        'apptime' => round(microtime(true) * 1000),
        'appuser' => 'jobfinder_user',
        'amount' => $amount,
        'description' => $description,
        'bankcode' => '',
        'embeddata' => json_encode([]),
        'item' => json_encode([]),
        'callback_url' => $callbackUrl,
    ];

    // Tạo mã xác thực (mac)
    $data = implode('|', [
        $params['appid'],
        $params['apptransid'],
        $params['appuser'],
        $params['amount'],
        $params['apptime'],
        $params['embeddata'],
        $params['item']
    ]);
    $params['mac'] = hash_hmac('sha256', $data, $key1);

    // Log tham số gửi đi để kiểm tra
    Log::info('ZaloPay request params: ' . json_encode($params));

    // Gọi API ZaloPay để tạo đơn hàng
    $response = Http::asForm()->post($createOrderUrl, $params);

    // Log phản hồi từ ZaloPay
    Log::info('ZaloPay response status: ' . $response->status());
    Log::info('ZaloPay response body: ' . $response->body());

    if ($response->successful()) {
        $responseData = $response->json();
        if (isset($responseData['return_code']) && $responseData['return_code'] == 1) {
            return response()->json([
                'payment_url' => $responseData['orderurl'],
                'app_trans_id' => $appTransId,
            ], 200);
        } else {
            Log::error('ZaloPay create order failed: Invalid returncode or response format', [
                'response' => $responseData
            ]);
            return response()->json([
                'error' => 'Không thể tạo đơn hàng: ' . ($responseData['return_message'] ?? 'Unknown error'),
                'sub_error' => $responseData['sub_return_message'] ?? 'Unknown sub error'
            ], 500);
        }
    } else {
        Log::error('ZaloPay create order failed: HTTP error', [
            'status' => $response->status(),
            'body' => $response->body()
        ]);
        return response()->json(['error' => 'Không thể tạo đơn hàng: HTTP ' . $response->status()], 500);
    }
}

    public function zaloPayCallback(Request $request)
    {
        $key2 = env('ZALOPAY_KEY2');
        $data = $request->input('data');
        $mac = $request->input('mac');

        // Kiểm tra tính hợp lệ của callback
        $computedMac = hash_hmac('sha256', $data, $key2);
        if ($computedMac !== $mac) {
            return response()->json(['error' => 'Invalid mac'], 400);
        }

        $data = json_decode($request->input('data'), true);
        $appTransId = $data['app_trans_id'];
        $amount = $data['amount'];
        $status = $data['status'];

        if ($status == 1) {
            // Thanh toán thành công
            Log::info("Payment successful for app_trans_id: $appTransId, amount: $amount");
            // TODO: Gửi thông báo cho ứng dụng Android (qua API hoặc Firebase Cloud Messaging)
        } else {
            Log::error("Payment failed for app_trans_id: $appTransId");
        }

        return response()->json(['status' => 'ok']);
    }
}