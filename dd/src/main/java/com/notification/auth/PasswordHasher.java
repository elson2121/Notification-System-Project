package com.notification.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    public static String simpleHash(String password) {
        try {
            if (password == null) {
                return "";
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("❌ Error hashing password: " + e.getMessage());
            System.err.println("⚠ WARNING: Using plain text password (for development only)");
            return password; // Fallback for development
        }
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        String hashedInput = simpleHash(plainPassword);
        return hashedInput.equals(hashedPassword);
    }
}