package com.notification.dashboard.util;

import java.net.URL;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class StyleManager {
    
    // CSS file paths
    public static final String LOGIN_CSS = "/css/login.css";
    public static final String MAIN_CSS = "/css/style.css";
    public static final String THEME_CSS = "/css/theme.css";
    
    /**
     * Load CSS file for a scene
     */
    public static void loadStyles(Scene scene, String... cssFiles) {
        if (scene == null) return;
        
        scene.getStylesheets().clear();
        
        for (String cssFile : cssFiles) {
            try {
                URL cssUrl = StyleManager.class.getResource(cssFile);
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("Loaded CSS: " + cssFile);
                } else {
                    System.err.println("CSS file not found: " + cssFile);
                }
            } catch (Exception e) {
                System.err.println("Error loading CSS: " + cssFile + " - " + e.getMessage());
            }
        }
    }
    
    /**
     * Load CSS file for a parent node
     */
    public static void loadStyles(Parent parent, String... cssFiles) {
        if (parent == null || parent.getScene() == null) return;
        loadStyles(parent.getScene(), cssFiles);
    }
}