package com.example.jobfinder01.Zalopay.helpers;

import android.annotation.SuppressLint;

import com.example.jobfinder01.Zalopay.hmac.HMacUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helpers {
    private static int transIdDefault = 1;

    @SuppressLint("DefaultLocale")
    public static String getAppTransId() {
        if (transIdDefault >= 100000) {
            transIdDefault = 1;
        }
        transIdDefault += 1;
        SimpleDateFormat formatDateTime = new SimpleDateFormat("yyMMdd_hhmmss");
        String timeString = formatDateTime.format(new Date());
        return String.format("%s%06d", timeString, transIdDefault);
    }

    public static String getMac(String key, String data) throws Exception {
        return HMacUtil.HMacHexStringEncode(HMacUtil.HMACSHA256, key, data);
    }
}