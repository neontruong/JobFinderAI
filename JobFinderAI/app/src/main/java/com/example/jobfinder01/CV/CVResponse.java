package com.example.jobfinder01.CV;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CVResponse {
    @SerializedName("data")
    private List<CV> data;

    public List<CV> getData() {
        return data;
    }

    public void setData(List<CV> data) {
        this.data = data;
    }
}