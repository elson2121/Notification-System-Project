package com.notification.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Updated with proper connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/java?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "ELSONDI@1234";

    private static DatabaseConnection instance;

    // Don't store a single connection - let each thread get its own
    private DatabaseConnection() {
        // Initialize driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Registered Successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found!");
            System.err.println("Please add MySQL Connector/J to your classpath.");
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Always create a new connection for each request
            // In a real application, use a connection pool (HikariCP, etc.)
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Database connection established successfully");
            return connection;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to connect to database!");
            System.err.println("URL: " + JDBC_URL);
            System.err.println("Username: " + USERNAME);
            System.err.println("Possible causes:");
            System.err.println("1. MySQL server is not running");
            System.err.println("2. Database 'java' doesn't exist");
            System.err.println("3. Incorrect username/password");
            System.err.println("4. Firewall blocking port 3306");
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    // Helper method to test the connection
    public static boolean testConnection() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
        }
    }
}