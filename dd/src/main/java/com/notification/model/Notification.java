// File: src/main/java/com/notification/model/Notification.java
package com.notification.model;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String title;
    private String message;
    private String channel; // EMAIL, SMS, PUSH, WHATSAPP
    private String status; // PENDING, SENT, DELIVERED, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private String recipientEmail;
    private String recipientPhone;
    private String errorMessage;
    
    // Constructors
    public Notification() {}
    
    public Notification(String title, String message, String channel, int userId) {
        this.title = title;
        this.message = message;
        this.channel = channel;
        this.userId = userId;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters (generate these or write manually)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    
    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", channel='" + channel + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}