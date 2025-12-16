package com.notification.auth;

import java.util.regex.Pattern;

public class UserValidator {
    
    // Validation patterns
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");
    
    public static ValidationResult validateUsername(String username) {
        ValidationResult result = new ValidationResult();
        
        if (username == null || username.trim().isEmpty()) {
            result.setValid(false);
            result.setMessage("Username is required");
            return result;
        }
        
        String trimmedUsername = username.trim();
        
        if (trimmedUsername.length() < 3) {
            result.setValid(false);
            result.setMessage("Username must be at least 3 characters");
            return result;
        }
        
        if (trimmedUsername.length() > 20) {
            result.setValid(false);
            result.setMessage("Username cannot exceed 20 characters");
            return result;
        }
        
        if (!USERNAME_PATTERN.matcher(trimmedUsername).matches()) {
            result.setValid(false);
            result.setMessage("Username can only contain letters, numbers, and underscores");
            return result;
        }
        
        result.setValid(true);
        return result;
    }
    
    public static ValidationResult validateEmail(String email) {
        ValidationResult result = new ValidationResult();
        
        if (email == null || email.trim().isEmpty()) {
            result.setValid(false);
            result.setMessage("Email is required");
            return result;
        }
        
        String trimmedEmail = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            result.setValid(false);
            result.setMessage("Please enter a valid email address");
            return result;
        }
        
        result.setValid(true);
        return result;
    }
    
    public static ValidationResult validatePassword(String password) {
        ValidationResult result = new ValidationResult();
        
        if (password == null || password.isEmpty()) {
            result.setValid(false);
            result.setMessage("Password is required");
            return result;
        }
        
        if (password.length() < 6) {
            result.setValid(false);
            result.setMessage("Password must be at least 6 characters");
            return result;
        }
        
        // Check password strength
        int strength = calculatePasswordStrength(password);
        
        result.setValid(true);
        result.setStrength(strength);
        
        if (strength < 2) {
            result.setMessage("Weak password");
        } else if (strength < 3) {
            result.setMessage("Fair password");
        } else if (strength < 4) {
            result.setMessage("Good password");
        } else {
            result.setMessage("Strong password");
        }
        
        return result;
    }
    
    public static ValidationResult validateConfirmPassword(String password, String confirmPassword) {
        ValidationResult result = new ValidationResult();
        
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            result.setValid(false);
            result.setMessage("Please confirm your password");
            return result;
        }
        
        if (!password.equals(confirmPassword)) {
            result.setValid(false);
            result.setMessage("Passwords do not match");
            return result;
        }
        
        result.setValid(true);
        return result;
    }
    
    private static int calculatePasswordStrength(String password) {
        int strength = 0;
        
        // Check length
        if (password.length() >= 8) strength++;
        
        // Check for uppercase letters
        if (password.matches(".*[A-Z].*")) strength++;
        
        // Check for lowercase letters
        if (password.matches(".*[a-z].*")) strength++;
        
        // Check for numbers
        if (password.matches(".*[0-9].*")) strength++;
        
        // Check for special characters
        if (password.matches(".*[^A-Za-z0-9].*")) strength++;
        
        return strength;
    }
    
    public static class ValidationResult {
        private boolean valid;
        private String message;
        private int strength;
        
        public ValidationResult() {
            this.valid = false;
            this.message = "";
            this.strength = 0;
        }
        
        // Getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public int getStrength() { return strength; }
        public void setStrength(int strength) { this.strength = strength; }
    }
}