package com.notification;

import com.notification.dashboard.controller.MainController;
import com.notification.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {
    
    private static User currentUser; // Store logged in user
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Load login screen first
            loadLoginScreen(primaryStage);
            
        } catch (Exception e) {
            System.err.println("❌ Application startup error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void loadLoginScreen(Stage stage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/notification/dashboard/view/login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            // Load CSS
            try {
                URL cssUrl = Main.class.getResource("/css/login.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("✅ CSS loaded successfully for login screen");
                } else {
                    System.err.println("❌ CSS not found for login screen");
                }
            } catch (Exception e) {
                System.err.println("❌ Error loading CSS: " + e.getMessage());
            }
            
            stage.setScene(scene);
            stage.setTitle("Instant Notification Engine - Login");
            
            // Set size constraints
            stage.setMinWidth(800);
            stage.setMinHeight(500);
            stage.setWidth(1000);
            stage.setHeight(650);
            
            stage.show();
            
            System.out.println("✅ Login screen loaded successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading login screen: " + e.getMessage());
            throw e;
        }
    }
    
    // NEW METHOD: Load main dashboard after successful login
    public static void loadMainDashboard(Stage stage, User user) throws Exception {
        currentUser = user;
        
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/notification/dashboard/view/main.fxml"));
            Parent root = loader.load();
            
            // Get controller and set user
            MainController controller = loader.getController();
            controller.setCurrentUser(user);
            
            Scene scene = new Scene(root);
            
            // Load CSS for dashboard
            try {
                URL cssUrl = Main.class.getResource("/css/login.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("✅ CSS loaded successfully for dashboard");
                }
            } catch (Exception e) {
                System.err.println("❌ Error loading CSS for dashboard: " + e.getMessage());
            }
            
            stage.setScene(scene);
            stage.setTitle("Dashboard - Notification Engine");
            
            // Set dashboard size (larger than login)
            stage.setMinWidth(1000);
            stage.setMinHeight(600);
            stage.setWidth(1200);
            stage.setHeight(800);
            
            stage.show();
            
            System.out.println("✅ Dashboard loaded successfully for user: " + user.getUsername());
            
        } catch (Exception e) {
            System.err.println("❌ Error loading dashboard: " + e.getMessage());
            throw e;
        }
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    // Test method (optional - for debugging)
    public static void testAuthentication() {
        try {
            com.notification.auth.AuthService authService = new com.notification.auth.AuthService();
            
            // Test authentication (use your test credentials)
            String testUsername = "admin";
            String testPassword = "password123";
            
            // FIX: Use the login() method which returns boolean
            boolean success = authService.login(testUsername, testPassword);
            
            if (success) {
                // Get the user from session manager
                com.notification.auth.SessionManager session = com.notification.auth.SessionManager.getInstance();
                User user = session.getCurrentUser();
                
                if (user != null) {
                    System.out.println("✅ Authentication test successful!");
                    System.out.println("   User: " + user.getUsername());
                    System.out.println("   Role: " + user.getRole());
                    System.out.println("   Email: " + user.getEmail());
                } else {
                    System.out.println("⚠️ Login successful but user not in session");
                }
            } else {
                System.out.println("❌ Authentication test failed");
                System.out.println("   Make sure you have a user in database:");
                System.out.println("   Username: admin, Password: password123");
            }
        } catch (Exception e) {
            System.err.println("❌ Test error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Alternative test method if you want to test the authenticate() method directly
    public static void testDirectAuthentication() {
        try {
            com.notification.database.UserDAO userDAO = new com.notification.database.UserDAO();
            
            // Hash the password first
            String hashedPassword = com.notification.auth.PasswordHasher.simpleHash("password123");
            
            // Test directly with UserDAO
            User user = userDAO.authenticate("admin", hashedPassword);
            
            if (user != null) {
                System.out.println("✅ Direct authentication test successful!");
                System.out.println("   User: " + user.getUsername());
                System.out.println("   Role: " + user.getRole());
                System.out.println("   Email: " + user.getEmail());
            } else {
                System.out.println("❌ Direct authentication test failed");
                System.out.println("   Check database and password hash");
            }
        } catch (Exception e) {
            System.err.println("❌ Direct test error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // Uncomment to test authentication before starting app
        // testAuthentication();
        // OR
        // testDirectAuthentication();
        
        // Start the application
        launch(args);
    }
}