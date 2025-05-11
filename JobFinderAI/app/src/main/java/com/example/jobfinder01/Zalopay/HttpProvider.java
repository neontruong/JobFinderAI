package com.example.jobfinder01.Zalopay;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

public class HttpProvider {
    public static JSONObject sendPost(String URL, RequestBody formBody) {
        Log.d("HttpProvider", "Sending POST request to: " + URL);
        JSONObject data = new JSONObject();
        try {
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                    .build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(spec))
                    .callTimeout(5000, TimeUnit.MILLISECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(URL)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(formBody)
                    .build();

            Log.d("HttpProvider", "Executing request...");
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                Log.e("HttpProvider", "Request failed with code: " + response.code() + ", body: " + errorBody);
                return null;
            }

            String responseBody = response.body().string();
            Log.d("HttpProvider", "Response body: " + responseBody);
            data = new JSONObject(responseBody);
        } catch (IOException e) {
            Log.e("HttpProvider", "IOException in sendPost: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.e("HttpProvider", "JSONException in sendPost: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            Log.e("HttpProvider", "Unexpected error in sendPost: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
            e.printStackTrace();
            return null;
        }
        return data;
    }
}