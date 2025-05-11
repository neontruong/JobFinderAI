package com.example.jobfinder01.ui.notifications;

import java.io.Serializable;

public class Notification implements Serializable {
    public String message;
    public String timestamp;

    public Notification(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}