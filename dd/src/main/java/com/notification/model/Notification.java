package com.notification.model;

import java.time.LocalDateTime;

public class Notification {

    private int id;
    private String title;
    private String content;
    private int senderId;
    private int recipientId;
    private LocalDateTime timestamp;
    private String channel; // EMAIL, SMS, PUSH
    private String status; // PENDING, SENT, FAILED

    public Notification(int id, String title, String content, int senderId, int recipientId, LocalDateTime timestamp, String channel, String status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.timestamp = timestamp;
        this.channel = channel;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getRecipientId() { return recipientId; }
    public void setRecipientId(int recipientId) { this.recipientId = recipientId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}