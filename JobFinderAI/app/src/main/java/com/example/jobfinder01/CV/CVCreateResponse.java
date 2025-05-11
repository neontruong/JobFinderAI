package com.example.jobfinder01.CV;

import com.google.gson.annotations.SerializedName;

public class CVCreateResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private CV data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CV getData() {
        return data;
    }

    public void setData(CV data) {
        this.data = data;
    }
}