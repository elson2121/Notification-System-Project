package com.notification.dashboard.controller;

import com.notification.database.UserDAO;
import com.notification.database.NotificationDAO;
import com.notification.model.User;
import com.notification.model.Notification;
import com.notification.engine.EmailService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

public class MainController {

    @FXML private Label welcomeLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label userGreetingLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label notificationsTodayLabel;
    @FXML private Label successRateLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<Notification> recentNotificationsTable;
    @FXML private TableColumn<Notification, String> timeColumn;
    @FXML private TableColumn<Notification, String> titleColumn;
    @FXML private TableColumn<Notification, String> channelColumn;
    @FXML private TableColumn<Notification, String> statusColumn;

    @FXML private TableView<Notification> notificationsTable;
    @FXML private TableColumn<Notification, Integer> idColumn;
    @FXML private TableColumn<Notification, String> dateColumn;
    @FXML private TableColumn<Notification, String> notificationTitleColumn;
    @FXML private TableColumn<Notification, String> messageColumn;
    @FXML private TableColumn<Notification, String> notificationChannelColumn;
    @FXML private TableColumn<Notification, String> notificationStatusColumn;

    @FXML private Tab sendNotificationTab;
    @FXML private RadioButton allUsersRadio;
    @FXML private RadioButton adminsOnlyRadio;
    @FXML private RadioButton specificUserRadio;
    @FXML private TextField notificationTitleField;
    @FXML private TextArea notificationMessageField;
    @FXML private CheckBox emailCheckbox;
    @FXML private CheckBox smsCheckbox;
    @FXML private CheckBox pushCheckbox;
    @FXML private Label sendStatusLabel;

    @FXML private TabPane mainTabPane;

    private User currentUser;
    private UserDAO userDAO;
    private NotificationDAO notificationDAO;
    private EmailService emailService;

    private ObservableList<Notification> recentNotificationsData = FXCollections.observableArrayList();
    private ObservableList<Notification> allNotificationsData = FXCollections.observableArrayList();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        initializeUserData();
    }

    @FXML
    public void initialize() {
        // Initialize DAOs
        this.userDAO = new UserDAO();
        this.notificationDAO = new NotificationDAO();
        this.emailService = new EmailService();

        // Setup table columns for recent notifications
        timeColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime timestamp = cellData.getValue().getTimestamp();
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                    timestamp != null ? timestamp.format(formatter) : "N/A");
        });

        titleColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getTitle()));

        channelColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getChannel()));

        statusColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getStatus()));

        recentNotificationsTable.setItems(recentNotificationsData);

        // Setup table columns for all notifications
        idColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createObjectBinding(() ->
                        cellData.getValue().getId()));

        dateColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime timestamp = cellData.getValue().getTimestamp();
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                    timestamp != null ? timestamp.format(formatter) : "N/A");
        });

        notificationTitleColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getTitle()));

        messageColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    String msg = cellData.getValue().getContent();
                    return msg != null && msg.length() > 50 ? msg.substring(0, 50) + "..." : msg;
                }));

        notificationChannelColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getChannel()));

        notificationStatusColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getStatus()));

        notificationsTable.setItems(allNotificationsData);

        // Enable/disable send notification tab based on user role
        if (currentUser != null && currentUser.getRole().equals("admin")) {
            sendNotificationTab.setDisable(false);
        }

        statusLabel.setText("Dashboard loaded successfully");
    }

    private void initializeUserData() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername());
            userRoleLabel.setText(currentUser.getRole().toUpperCase());
            userGreetingLabel.setText("Hello " + currentUser.getUsername() +
                    "! You have successfully logged into the Notification Engine.");

            // Load dashboard statistics
            loadDashboardStats();

            // Load user's recent notifications
            loadRecentNotifications();

            // Load all notifications for the user
            loadAllNotifications();
        }
    }

    private void loadDashboardStats() {
        try {
            // Get total users count (for admins only)
            if (currentUser.getRole().equals("admin")) {
                // Assuming UserDAO has a getUserCount() method
                // totalUsersLabel.setText(String.valueOf(userDAO.getUserCount()));
                totalUsersLabel.setText("142"); // Temporary placeholder
            } else {
                totalUsersLabel.setText("N/A");
            }

            // Get today's notification count
            // You'll need to implement this method in NotificationDAO
            // int todayCount = notificationDAO.getTodaysNotificationCount();
            int todayCount = 15; // Temporary placeholder
            notificationsTodayLabel.setText(String.valueOf(todayCount));

            // Calculate success rate (simplified)
            successRateLabel.setText("95%");

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading dashboard stats");
        }
    }

    private void loadRecentNotifications() {
        try {
            List<Notification> notifications = notificationDAO.getUserNotifications(currentUser.getId());
            // Get only the last 5 for the dashboard
            int limit = Math.min(5, notifications.size());
            recentNotificationsData.setAll(notifications.subList(0, limit));
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading recent notifications");
        }
    }

    private void loadAllNotifications() {
        try {
            List<Notification> notifications = notificationDAO.getUserNotifications(currentUser.getId());
            allNotificationsData.setAll(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading notifications");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Clear user session
            currentUser = null;

            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/notification/dashboard/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Notification Engine");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendTestEmail() {
        try {
            // Create a test notification using the correct constructor
            // Based on the error, the constructor needs: int id, String title, String content, int userId,
            // int recipientId, LocalDateTime timestamp, String channel, String status
            // For new notification, we can use 0 for id and let DB auto-increment
            Notification testNotification = new Notification(
                    0, // id - will be set by database
                    "Test Email from Notification Engine", // title
                    "This is a test email sent from the Notification Engine dashboard.", // content
                    currentUser.getId(), // userId
                    currentUser.getId(), // recipientId (same as sender for test)
                    LocalDateTime.now(), // timestamp
                    "EMAIL", // channel
                    "PENDING" // initial status
            );

            // Save to database
            notificationDAO.saveNotification(testNotification);

            // Send email
            boolean success = emailService.sendEmail(
                    currentUser.getEmail(),
                    testNotification.getTitle(),
                    testNotification.getContent()
            );

            // Update status
            String status = success ? "SENT" : "FAILED";
            notificationDAO.updateNotificationStatus(
                    testNotification.getId(),
                    status,
                    success ? null : "Failed to send email"
            );

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Test email sent to " + currentUser.getEmail());
                statusLabel.setText("Test email sent successfully");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to send test email");
                statusLabel.setText("Failed to send test email");
            }

            // Refresh notifications
            loadRecentNotifications();
            loadAllNotifications();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Error sending test email: " + e.getMessage());
        }
    }

    @FXML
    private void sendTestSMS() {
        showAlert(Alert.AlertType.INFORMATION, "Coming Soon",
                "SMS functionality will be implemented in the next version");
        statusLabel.setText("SMS functionality coming soon");
    }

    @FXML
    private void openSendNotification() {
        mainTabPane.getSelectionModel().select(sendNotificationTab);
    }

    @FXML
    private void showNotificationHistory() {
        mainTabPane.getSelectionModel().select(1); // Switch to My Notifications tab
    }

    @FXML
    private void refreshNotifications() {
        loadAllNotifications();
        statusLabel.setText("Notifications refreshed");
    }

    @FXML
    private void sendNotification() {
        try {
            // Validate inputs
            if (notificationTitleField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a title");
                return;
            }

            if (notificationMessageField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a message");
                return;
            }

            if (!emailCheckbox.isSelected() && !smsCheckbox.isSelected() && !pushCheckbox.isSelected()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select at least one channel");
                return;
            }

            // Determine recipients
            String recipientType = "ALL_USERS";
            if (adminsOnlyRadio.isSelected()) {
                recipientType = "ADMINS_ONLY";
            } else if (specificUserRadio.isSelected()) {
                recipientType = "SPECIFIC_USER";
            }

            // Create notification using the correct constructor
            Notification notification = new Notification(
                    0, // id - will be set by database
                    notificationTitleField.getText(), // title
                    notificationMessageField.getText(), // content
                    currentUser.getId(), // userId
                    currentUser.getId(), // recipientId (for now, send to self)
                    LocalDateTime.now(), // timestamp
                    emailCheckbox.isSelected() ? "EMAIL" : "SMS", // channel
                    "PENDING" // initial status
            );

            // Save to database
            notificationDAO.saveNotification(notification);

            // Send based on selected channels
            if (emailCheckbox.isSelected()) {
                boolean emailSuccess = emailService.sendEmail(
                        currentUser.getEmail(),
                        notification.getTitle(),
                        notification.getContent()
                );

                notificationDAO.updateNotificationStatus(
                        notification.getId(),
                        emailSuccess ? "SENT" : "FAILED",
                        emailSuccess ? null : "Email sending failed"
                );
            }

            // Clear form
            notificationTitleField.clear();
            notificationMessageField.clear();

            sendStatusLabel.setText("Notification sent successfully!");
            statusLabel.setText("Notification sent to " + currentUser.getEmail());

            // Refresh notifications
            loadRecentNotifications();
            loadAllNotifications();

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Notification sent successfully");

        } catch (Exception e) {
            e.printStackTrace();
            sendStatusLabel.setText("Error sending notification");
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Error sending notification: " + e.getMessage());
        }
    }

    @FXML
    private void openSettings() {
        showAlert(Alert.AlertType.INFORMATION, "Settings",
                "Settings functionality will be implemented soon");
    }

    @FXML
    private void showAbout() {
        showAlert(Alert.AlertType.INFORMATION, "About Notification Engine",
                "Notification Engine v1.0\n" +
                        "A Java-based notification system\n" +
                        "Supports Email, SMS, and Push notifications\n" +
                        "Developed for instant communication needs");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}