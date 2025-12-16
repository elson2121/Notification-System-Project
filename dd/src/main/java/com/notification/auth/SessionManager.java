package com.notification.auth;

import com.notification.model.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {
        // Private constructor for singleton
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public void clearCurrentUser() {
        this.currentUser = null;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public String getUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
}