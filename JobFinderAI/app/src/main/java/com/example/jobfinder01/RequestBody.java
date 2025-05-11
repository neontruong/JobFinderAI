package com.example.jobfinder01;

import com.google.gson.annotations.SerializedName;

public class RequestBody {
    @SerializedName("contents")
    private Content[] contents;

    public RequestBody(String text) {
        this.contents = new Content[]{
                new Content(new Part[]{
                        new Part(text) // Chỉ gửi nội dung tin nhắn, không thêm prompt
                })
        };
    }

    private static class Content {
        @SerializedName("parts")
        private Part[] parts;

        public Content(Part[] parts) {
            this.parts = parts;
        }
    }

    private static class Part {
        @SerializedName("text")
        private String text;

        public Part(String text) {
            this.text = text;
        }
    }
}