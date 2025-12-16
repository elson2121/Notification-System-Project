package com.notification;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.notification.database.DatabaseConnection;
import com.notification.session.SessionManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection first
            System.out.println("=== Starting Instant Notification Engine ===");
            System.out.println("Testing database connection...");
            boolean dbConnected = testDatabaseConnection();

            if (!dbConnected) {
                showDatabaseErrorDialog();
                return;
            }

            // Clear any existing session (for fresh start)
            SessionManager.getInstance().clearSession();

            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/notification/dashboard/view/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 700);

            // Add CSS if you have it
            try {
                String cssPath = "/com/notification/dashboard/view/styles.css";
                if (getClass().getResource(cssPath) != null) {
                    scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                    System.out.println("✓ CSS styles loaded successfully");
                }
            } catch (Exception e) {
                System.out.println("ℹ CSS file not found, continuing without styles.");
            }

            primaryStage.setTitle("Instant Notification Engine - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);

            // Set application icon if you have one
            try {
                String iconPath = "/com/notification/dashboard/view/icon.png";
                if (getClass().getResourceAsStream(iconPath) != null) {
                    Image icon = new Image(getClass().getResourceAsStream(iconPath));
                    primaryStage.getIcons().add(icon);
                    System.out.println("✓ Application icon loaded");
                }
            } catch (Exception e) {
                System.out.println("ℹ Icon not found, continuing without icon.");
            }

            primaryStage.show();
            System.out.println("✓ Application started successfully");

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Failed to start application: " + e.getMessage());
        }
    }

    private boolean testDatabaseConnection() {
        try {
            // Test the connection
            var connection = DatabaseConnection.getInstance().getConnection();
            if (connection != null && !connection.isClosed()) {
                System.out.println("✓ Database connection successful!");
                connection.close();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            return false;
        }
    }

    private void showDatabaseErrorDialog() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Database Connection Error");
        alert.setHeaderText("Cannot Connect to Database");
        alert.setContentText(
                "Failed to connect to MySQL database.\n\n" +
                        "Please ensure:\n" +
                        "1. MySQL server is running (Check in Services or Task Manager)\n" +
                        "2. Database 'java' exists (Run: CREATE DATABASE java;)\n" +
                        "3. Username: root, Password: ELSONDI@1234\n" +
                        "4. Port 3306 is accessible (Check firewall settings)\n" +
                        "5. MySQL Connector/J is in classpath\n\n" +
                        "Application will now exit."
        );
        alert.showAndWait();
        System.exit(1);
    }

    private void showErrorDialog(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Application Error");
        alert.setHeaderText("Failed to Start Application");
        alert.setContentText(message);
        alert.showAndWait();
        System.exit(1);
    }

    /**
     * Loads the main dashboard after successful login
     * @param stage The current stage
     * @param user The logged-in user
     */
    public static void loadMainDashboard(Stage stage, com.notification.model.User user) throws Exception {
        System.out.println("Loading dashboard for user: " + user.getUsername());

        // Set user in SessionManager
        SessionManager.getInstance().setCurrentUser(user);

        // Load the dashboard FXML
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/notification/dashboard/view/main.fxml"));
        Parent root = loader.load();

        // Get the controller and set the user
        com.notification.dashboard.controller.DashboardController controller = loader.getController();

        // Check if controller has setCurrentUser method and call it
        if (controller != null) {
            try {
                // Use reflection to check if setCurrentUser method exists
                java.lang.reflect.Method method = controller.getClass().getMethod("setCurrentUser", com.notification.model.User.class);
                method.invoke(controller, user);
                System.out.println("✓ User set via setCurrentUser method");
            } catch (NoSuchMethodException e) {
                // Controller doesn't have setCurrentUser method
                System.out.println("ℹ DashboardController doesn't have setCurrentUser method");
                System.out.println("ℹ Using SessionManager for user context");
            } catch (Exception e) {
                System.err.println("⚠ Error setting user in controller: " + e.getMessage());
            }
        }

        // Create scene
        Scene scene = new Scene(root, 1300, 800);

        // Add dashboard CSS if available
        try {
            String cssPath = "/com/notification/dashboard/view/dashboard.css";
            if (Main.class.getResource(cssPath) != null) {
                scene.getStylesheets().add(Main.class.getResource(cssPath).toExternalForm());
                System.out.println("✓ Dashboard CSS loaded");
            }
        } catch (Exception e) {
            System.out.println("ℹ Dashboard CSS not found.");
        }

        // Configure stage
        stage.setTitle("Instant Notification Engine - Dashboard (" + user.getUsername() + ")");
        stage.setScene(scene);
        stage.setMaximized(true);

        // Set dashboard icon if available
        try {
            String iconPath = "/com/notification/dashboard/view/dashboard-icon.png";
            if (Main.class.getResourceAsStream(iconPath) != null) {
                Image icon = new Image(Main.class.getResourceAsStream(iconPath));
                stage.getIcons().add(icon);
            }
        } catch (Exception e) {
            // Ignore if icon not found
        }

        stage.show();
        System.out.println("✓ Dashboard loaded successfully");
    }

    /**
     * Logs out and returns to login screen
     * @param stage The current stage
     */
    public static void logoutToLogin(Stage stage) {
        try {
            // Clear session
            SessionManager.getInstance().clearSession();

            // Load login screen
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/notification/dashboard/view/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 700);

            // Add CSS if available
            try {
                String cssPath = "/com/notification/dashboard/view/styles.css";
                if (Main.class.getResource(cssPath) != null) {
                    scene.getStylesheets().add(Main.class.getResource(cssPath).toExternalForm());
                }
            } catch (Exception e) {
                // Ignore if CSS not found
            }

            stage.setTitle("Instant Notification Engine - Login");
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setWidth(1200);
            stage.setHeight(700);

            System.out.println("✓ Logged out successfully");

        } catch (Exception e) {
            e.printStackTrace();
            showStaticErrorDialog("Logout Error", "Failed to logout: " + e.getMessage());
        }
    }

    /**
     * Shows an error dialog from static context
     */
    private static void showStaticErrorDialog(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Main entry point for the application
     */
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   INSTANT NOTIFICATION ENGINE v1.0     ");
        System.out.println("=========================================");

        // Check if JavaFX modules are available
        try {
            Class.forName("javafx.application.Application");
            System.out.println("✓ JavaFX modules detected");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ JavaFX modules not found!");
            System.err.println("Please ensure JavaFX is in your module path.");
            System.err.println("VM Options should include: --module-path \"path/to/javafx/lib\" --add-modules javafx.controls,javafx.fxml");
            return;
        }

        // Launch the JavaFX application
        launch(args);
    }
}