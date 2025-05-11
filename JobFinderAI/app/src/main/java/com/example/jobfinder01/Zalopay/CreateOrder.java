package com.example.jobfinder01.Zalopay;

import org.json.JSONObject;
import java.util.Date;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import android.util.Log;

import com.example.jobfinder01.Zalopay.AppInfo;
import com.example.jobfinder01.Zalopay.HttpProvider;
import com.example.jobfinder01.Zalopay.helpers.Helpers;

public class CreateOrder {
    private class CreateOrderData {
        String AppId;
        String AppUser;
        String AppTime;
        String Amount;
        String AppTransId;
        String EmbedData;
        String Items;
        String BankCode;
        String Description;
        String Mac;

        private CreateOrderData(String amount) throws Exception {
            Log.d("CreateOrder", "Creating order data...");
            long appTime = new Date().getTime();
            AppId = String.valueOf(AppInfo.APP_ID);
            AppUser = "Android_Demo";
            AppTime = String.valueOf(appTime);
            Amount = amount;
            AppTransId = Helpers.getAppTransId();
            EmbedData = "{}";
            Items = "[]";
            BankCode = "zalopayapp";
            Description = "Thanh toán để tiếp tục ứng tuyển #" + Helpers.getAppTransId();
            String inputHMac = String.format("%s|%s|%s|%s|%s|%s|%s",
                    this.AppId, this.AppTransId, this.AppUser, this.Amount,
                    this.AppTime, this.EmbedData, this.Items);
            Mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac);
            Log.d("CreateOrder", "Order data created: AppId=" + AppId + ", AppTransId=" + AppTransId);
        }
    }

    public JSONObject createOrder(String amount) throws Exception {
        Log.d("CreateOrder", "Starting createOrder with amount: " + amount);
        CreateOrderData input;
        try {
            input = new CreateOrderData(amount);
        } catch (Exception e) {
            Log.e("CreateOrder", "Error creating order data: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
            throw e;
        }

        RequestBody formBody = new FormBody.Builder()
                .add("app_id", input.AppId)
                .add("app_user", input.AppUser)
                .add("app_time", input.AppTime)
                .add("amount", input.Amount)
                .add("app_trans_id", input.AppTransId)
                .add("embed_data", input.EmbedData)
                .add("item", input.Items)
                .add("bank_code", input.BankCode)
                .add("description", input.Description)
                .add("mac", input.Mac)
                .build();

        Log.d("CreateOrder", "Sending request to ZaloPay...");
        JSONObject data = HttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody);
        if (data == null) {
            Log.e("CreateOrder", "HttpProvider.sendPost returned null");
        } else {
            Log.d("CreateOrder", "Response from ZaloPay: " + data.toString());
        }
        return data;
    }
}