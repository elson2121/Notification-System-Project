package com.notification.api;

import java.util.Map;

public class NotificationRequest {
    private String userId;
    private String message;
    private String channel;
    private String templateId;
    private Map<String, String> parameters;
    
    // Constructors
    public NotificationRequest() {}
    
    public NotificationRequest(String userId, String message, String channel) {
        this.userId = userId;
        this.message = message;
        this.channel = channel;
    }
    
    // Getters and Setters (በEclipse ውስጥ generate ማድረግ ትችላለህ)
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    
    public Map<String, String> getParameters() { return parameters; }
    public void setParameters(Map<String, String> parameters) { this.parameters = parameters; }
}
