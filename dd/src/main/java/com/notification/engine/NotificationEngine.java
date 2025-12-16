package com.notification.engine;

import com.notification.database.NotificationDAO;
import com.notification.database.UserDAO;
import com.notification.model.Notification;
import com.notification.model.User;
import java.util.List;

public class NotificationEngine {
 private final EmailService emailService;
 private final NotificationDAO notificationDAO;
 private final UserDAO userDAO;
 
 public NotificationEngine() {
     this.emailService = new EmailService();
     this.notificationDAO = new NotificationDAO();
     this.userDAO = new UserDAO();
 }
 
 public void sendNotification(Notification notification) {
     try {
         // Get user details
         User user = userDAO.getUserById(notification.getUserId());
         
         boolean success = false;
         String errorMessage = null;
         
         if ("EMAIL".equalsIgnoreCase(notification.getChannel())) {
             success = emailService.sendEmail(
                 user.getEmail(),
                 notification.getTitle(),
                 notification.getMessage()
             );
             
             if (!success) {
                 errorMessage = "Failed to send email";
             }
         } else if ("SMS".equalsIgnoreCase(notification.getChannel())) {
             // SMS will be implemented later
             errorMessage = "SMS service not yet implemented";
         } else if ("PUSH".equalsIgnoreCase(notification.getChannel())) {
             // Push notification will be implemented later
             errorMessage = "Push service not yet implemented";
         }
         
         // Update notification status
         String status = success ? "SENT" : "FAILED";
         notificationDAO.updateNotificationStatus(
             notification.getId(), 
             status, 
             errorMessage
         );
         
     } catch (Exception e) {
         e.printStackTrace();
         notificationDAO.updateNotificationStatus(
             notification.getId(), 
             "FAILED", 
             "System error: " + e.getMessage()
         );
     }
 }
 
 public void processPendingNotifications() {
     List<Notification> pendingNotifications = notificationDAO.getPendingNotifications();
     
     for (Notification notification : pendingNotifications) {
         sendNotification(notification);
     }
 }
}