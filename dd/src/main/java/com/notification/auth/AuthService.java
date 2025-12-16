package com.notification.auth;

import com.notification.database.UserDAO;
import com.notification.model.User;

public class AuthService {
    private final UserDAO userDAO;
    private final SessionManager sessionManager;
    
    public AuthService() {
        this.userDAO = new UserDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    public boolean login(String usernameOrEmail, String password) {
        try {
            String hashedPassword = PasswordHasher.simpleHash(password);
            User user = userDAO.authenticate(usernameOrEmail, hashedPassword);
            
            if (user != null) {
                sessionManager.setCurrentUser(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return false;
        }
    }
    
    // ADD THIS METHOD: Authenticate and return User object
    public User authenticateAndGetUser(String usernameOrEmail, String password) {
        try {
            String hashedPassword = PasswordHasher.simpleHash(password);
            User user = userDAO.authenticate(usernameOrEmail, hashedPassword);
            
            if (user != null) {
                sessionManager.setCurrentUser(user);
                return user;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            return null;
        }
    }
    
    public boolean register(String username, String email, String password, String role) {
        try {
            if (userDAO.usernameExists(username)) {
                throw new IllegalArgumentException("Username already exists");
            }
            
            if (userDAO.emailExists(email)) {
                throw new IllegalArgumentException("Email already exists");
            }
            
            if (password.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters");
            }
            
            String hashedPassword = PasswordHasher.simpleHash(password);
            User newUser = new User(username, email, hashedPassword, role);
            return userDAO.createUser(newUser);
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            throw e;
        }
    }
    
    public boolean register(String username, String email, String password) {
        return register(username, email, password, "user");
    }
    
    public void logout() {
        sessionManager.clearCurrentUser();
    }
    
    public boolean isAuthenticated() {
        return sessionManager.getCurrentUser() != null;
    }
    
    public boolean hasRole(String role) {
        User currentUser = sessionManager.getCurrentUser();
        return currentUser != null && currentUser.getRole().equalsIgnoreCase(role);
    }
    
    public User getCurrentUser() {
        return sessionManager.getCurrentUser();
    }
}