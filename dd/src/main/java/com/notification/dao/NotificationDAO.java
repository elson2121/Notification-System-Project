package com.notification.dao;

import com.notification.database.DatabaseConnection;
import com.notification.model.Notification;
import com.notification.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public void sendNotification(String title, String content, int senderId, int recipientId) throws SQLException {
        String sql = "INSERT INTO notifications (title, content, sender_id, recipient_id, timestamp, channel, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setInt(3, senderId);
            stmt.setInt(4, recipientId);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(6, "EMAIL"); // Default channel
            stmt.setString(7, "PENDING"); // Default status
            stmt.executeUpdate();
        }
    }

    public List<Notification> getNotificationsForUser(String username) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT n.* FROM notifications n JOIN users u ON n.recipient_id = u.id WHERE u.username = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new Notification(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getInt("sender_id"),
                            rs.getInt("recipient_id"),
                            rs.getTimestamp("timestamp").toLocalDateTime(),
                            rs.getString("channel"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return notifications;
    }
    
    public User findUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        }
        return null;
    }

    public void updateNotificationStatus(int notificationId, String status, String errorMessage) {
        String sql = "UPDATE notifications SET status = ?, error_message = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, errorMessage);
            stmt.setInt(3, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getPendingNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                notifications.add(new Notification(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("sender_id"),
                        rs.getInt("recipient_id"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getString("channel"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
}