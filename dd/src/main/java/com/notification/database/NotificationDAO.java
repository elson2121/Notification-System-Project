// File: src/main/java/com/notification/database/NotificationDAO.java
package com.notification.database;

import com.notification.model.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private final DatabaseConnection dbConnection;
    
    public NotificationDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // 1. Save a new notification
    public boolean saveNotification(Notification notification) {
        String sql = "INSERT INTO notifications " +
                    "(user_id, title, message, channel, status, created_at, recipient_email, recipient_phone) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getTitle());
            stmt.setString(3, notification.getMessage());
            stmt.setString(4, notification.getChannel());
            stmt.setString(5, notification.getStatus());
            stmt.setTimestamp(6, Timestamp.valueOf(notification.getCreatedAt()));
            stmt.setString(7, notification.getRecipientEmail());
            stmt.setString(8, notification.getRecipientPhone());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    notification.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error saving notification: " + e.getMessage());
            return false;
        }
    }
    
    // 2. Get all notifications for a user
    public List<Notification> getUserNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 50";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Notification notification = mapResultSetToNotification(rs);
                notifications.add(notification);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user notifications: " + e.getMessage());
        }
        return notifications;
    }
    
    // 3. Get all pending notifications
    public List<Notification> getPendingNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE status = 'PENDING' ORDER BY created_at ASC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Notification notification = mapResultSetToNotification(rs);
                notifications.add(notification);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting pending notifications: " + e.getMessage());
        }
        return notifications;
    }
    
    // 4. Update notification status
    public boolean updateNotificationStatus(int notificationId, String status, String errorMessage) {
        String sql = "UPDATE notifications SET status = ?, sent_at = NOW(), error_message = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setString(2, errorMessage);
            stmt.setInt(3, notificationId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating notification status: " + e.getMessage());
            return false;
        }
    }
    
    // 5. Get notification statistics
    public int getTodaysNotificationCount() {
        String sql = "SELECT COUNT(*) FROM notifications WHERE DATE(created_at) = CURDATE()";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            System.err.println("Error getting today's notification count: " + e.getMessage());
            return 0;
        }
    }
    
    // Helper method to map ResultSet to Notification object
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setChannel(rs.getString("channel"));
        notification.setStatus(rs.getString("status"));
        notification.setRecipientEmail(rs.getString("recipient_email"));
        notification.setRecipientPhone(rs.getString("recipient_phone"));
        notification.setErrorMessage(rs.getString("error_message"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            notification.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp sentAt = rs.getTimestamp("sent_at");
        if (sentAt != null) {
            notification.setSentAt(sentAt.toLocalDateTime());
        }
        
        return notification;
    }
}