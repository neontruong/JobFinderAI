package com.example.jobfinder01;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class MessageContent {
    // Lớp Part (phần tử văn bản)
    public static class Part {
        @SerializedName("text")
        private String text;

        public Part(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    // Lớp Content (chứa danh sách Part)
    public static class Content {
        @SerializedName("parts")
        private List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }

        public Content(String text) {
            this.parts = Collections.singletonList(new Part(text));
        }

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
    }
}