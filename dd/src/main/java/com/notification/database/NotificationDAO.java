package com.notification.database;

import com.notification.model.Notification;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private final DatabaseConnection dbConnection;

    public NotificationDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Saves a new notification to the database.
     * This assumes your 'notifications' table has columns like sender_id, recipient_id, etc.
     */
    public boolean saveNotification(Notification notification) {
        String sql = "INSERT INTO notifications " +
                     "(sender_id, recipient_id, title, content, channel, status, timestamp) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, notification.getSenderId()); // Corrected: Was getUserId
            stmt.setInt(2, notification.getRecipientId());
            stmt.setString(3, notification.getTitle());
            stmt.setString(4, notification.getContent()); // Corrected: Was getMessage
            stmt.setString(5, notification.getChannel());
            stmt.setString(6, notification.getStatus());
            stmt.setTimestamp(7, Timestamp.valueOf(notification.getTimestamp())); // Corrected: Was getCreatedAt

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
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

    /**
     * Gets all notifications for a specific user (as the recipient).
     */
    public List<Notification> getUserNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE recipient_id = ? ORDER BY timestamp DESC LIMIT 50";

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

    /**
     * Gets all notifications with a 'PENDING' status.
     */
    public List<Notification> getPendingNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE status = 'PENDING' ORDER BY timestamp ASC";

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

    /**
     * Updates the status of a notification (e.g., to 'SENT' or 'FAILED').
     */
    public boolean updateNotificationStatus(int notificationId, String status, String errorMessage) {
        String sql = "UPDATE notifications SET status = ?, error_message = ? WHERE id = ?";

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

    /**
     * Helper method to map a ResultSet row to a Notification object.
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String content = rs.getString("content");
        int senderId = rs.getInt("sender_id");
        int recipientId = rs.getInt("recipient_id");
        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
        String channel = rs.getString("channel");
        String status = rs.getString("status");

        return new Notification(id, title, content, senderId, recipientId, timestamp, channel, status);
    }
}