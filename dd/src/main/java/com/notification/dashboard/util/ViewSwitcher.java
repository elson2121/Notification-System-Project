package com.notification.dashboard.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewSwitcher {
    
    public static void switchToLoginView(Scene currentScene) {
        try {
            Parent root = FXMLLoader.load(
                ViewSwitcher.class.getResource("/com/notification/dashboard/view/login.fxml")
            );
            currentScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("View Error", "Cannot load login view: " + e.getMessage());
        }
    }
    
    public static void switchToMainView(Scene currentScene) {
        try {
            Parent root = FXMLLoader.load(
                ViewSwitcher.class.getResource("/com/notification/dashboard/view/main.fxml")
            );
            currentScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("View Error", "Cannot load main view: " + e.getMessage());
        }
    }
    
    public static void switchToView(String fxmlFile, Node node) {
        try {
            Parent root = FXMLLoader.load(
                ViewSwitcher.class.getResource("/com/notification/dashboard/view/" + fxmlFile)
            );
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("View Error", "Cannot load view: " + e.getMessage());
        }
    }
}