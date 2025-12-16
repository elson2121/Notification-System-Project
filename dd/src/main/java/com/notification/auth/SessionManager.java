package com.notification.session;

import com.notification.model.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {
        // Private constructor for singleton
        System.out.println("SessionManager initialized");
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
        System.out.println("✅ Session: User set to " + (user != null ? user.getUsername() + " (ID: " + user.getId() + ")" : "null"));
    }

    public void clearSession() {
        System.out.println("Session: Clearing user session");
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        boolean loggedIn = currentUser != null;
        System.out.println("Session check: User logged in = " + loggedIn);
        return loggedIn;
    }

    public String getUsername() {
        String username = currentUser != null ? currentUser.getUsername() : null;
        System.out.println("Session: Getting username = " + username);
        return username;
    }

    public String getUserRole() {
        String role = currentUser != null ? currentUser.getRole() : null;
        System.out.println("Session: Getting role = " + role);
        return role;
    }

    public int getUserId() {
        int userId = currentUser != null ? currentUser.getId() : -1;
        System.out.println("Session: Getting user ID = " + userId);
        return userId;
    }
}