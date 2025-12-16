package com.notification.dashboard.controller;

import com.notification.dao.NotificationDAO;
import com.notification.dashboard.util.AlertHelper;
import com.notification.model.Notification;
import com.notification.model.User;
import com.notification.session.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    @FXML
    private TextField titleField;
    @FXML
    private TextArea contentArea;
    @FXML
    private TextField recipientField;
    @FXML
    private Button sendButton;
    @FXML
    private TableView<Notification> notificationsTable;
    @FXML
    private TableColumn<Notification, String> titleColumn;
    @FXML
    private TableColumn<Notification, String> contentColumn;
    @FXML
    private TableColumn<Notification, String> dateColumn;

    private final NotificationDAO notificationDAO = new NotificationDAO();
    private User currentUser;

    // ADD THIS METHOD - This is what Main.java is trying to call
    public void setCurrentUser(User user) {
        this.currentUser = user;
        initializeDashboard();
    }

    @FXML
    public void initialize() {
        // This will be called by FXMLLoader automatically
        // We'll check if user is already set via SessionManager
        if (currentUser == null) {
            currentUser = SessionManager.getInstance().getCurrentUser();
        }

        if (currentUser == null) {
            AlertHelper.showError("Session Error", "No user logged in. Please log in again.");
            return;
        }

        initializeDashboard();
    }

    private void initializeDashboard() {
        setupTable();
        loadNotifications();

        if (sendButton != null) {
            sendButton.setOnAction(event -> sendNotification());
        }
    }

    private void setupTable() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));

        // Format date column
        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime timestamp = cellData.getValue().getTimestamp();
            if (timestamp != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return javafx.beans.binding.Bindings.createStringBinding(() ->
                        timestamp.format(formatter));
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "N/A");
        });
    }

    private void loadNotifications() {
        try {
            List<Notification> notifications = notificationDAO.getNotificationsForUser(currentUser.getUsername());
            notificationsTable.setItems(FXCollections.observableArrayList(notifications));
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Could not load notifications.");
            e.printStackTrace();
        }
    }

    @FXML
    private void sendNotification() {
        String title = titleField.getText();
        String content = contentArea.getText();
        String recipientUsername = recipientField.getText();

        if (title.isEmpty() || content.isEmpty() || recipientUsername.isEmpty()) {
            AlertHelper.showError("Input Error", "Please fill in all fields.");
            return;
        }

        try {
            User recipient = notificationDAO.findUserByUsername(recipientUsername);
            if (recipient == null) {
                AlertHelper.showError("Recipient Not Found", "The user '" + recipientUsername + "' does not exist.");
                return;
            }

            // FIXED: Call sendNotification (which returns void) and handle success
            notificationDAO.sendNotification(title, content, currentUser.getId(), recipient.getId());

            AlertHelper.showSuccess("Success", "Notification sent to " + recipientUsername + ".");

            // Clear the fields after sending
            titleField.clear();
            contentArea.clear();
            recipientField.clear();

            // Refresh the notifications for the current user
            loadNotifications();

        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Could not send notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Optional: Add a logout method
    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        try {
            // Load login screen
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/notification/dashboard/view/login.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) sendButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Login - Instant Notification Engine");
            stage.setMaximized(false);

        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "Failed to logout: " + e.getMessage());
        }
    }

    // Optional: Refresh button handler
    @FXML
    private void handleRefresh() {
        loadNotifications();
        AlertHelper.showInfo("Refreshed", "Notifications have been refreshed.");
    }

    // Optional: Clear form button handler
    @FXML
    private void handleClearForm() {
        titleField.clear();
        contentArea.clear();
        recipientField.clear();
        AlertHelper.showInfo("Cleared", "Form has been cleared.");
    }
}