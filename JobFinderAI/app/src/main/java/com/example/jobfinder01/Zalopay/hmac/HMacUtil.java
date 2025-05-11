package com.example.jobfinder01.Zalopay.hmac;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMacUtil {
    public static final String HMACSHA256 = "HmacSHA256";

    private static byte[] HMacEncode(final String algorithm, final String key, final String data) {
        try {
            Mac macGenerator = Mac.getInstance(algorithm);
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
            macGenerator.init(signingKey);
            return macGenerator.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String HMacHexStringEncode(final String algorithm, final String key, final String data) {
        byte[] hmacEncodeBytes = HMacEncode(algorithm, key, data);
        if (hmacEncodeBytes == null) return null;
        return HexStringUtil.byteArrayToHexString(hmacEncodeBytes);
    }
}