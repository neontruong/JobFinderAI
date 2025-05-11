package com.example.jobfinder01;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResponseBody {
    @SerializedName("candidates")
    private List<Candidate> candidates;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public static class Candidate {
        @SerializedName("content")
        private MessageContent.Content content;

        public MessageContent.Content getContent() {
            return content;
        }

        public void setContent(MessageContent.Content content) {
            this.content = content;
        }
    }
}