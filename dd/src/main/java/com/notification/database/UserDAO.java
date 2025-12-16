package com.notification.database;

import com.notification.model.User;
import java.sql.*;

public class UserDAO {
    private final DatabaseConnection dbConnection;
    
    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public User authenticate(String usernameOrEmail, String hashedPassword) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password_hash = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, hashedPassword);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                return user;
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return null;
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    public boolean createUser(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Create user error: " + e.getMessage());
            return false;
        } finally {
            closeResources(generatedKeys, stmt);
        }
    }
    
    public boolean usernameExists(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Check username error: " + e.getMessage());
            return false;
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    public boolean emailExists(String email) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Check email error: " + e.getMessage());
            return false;
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    public User getUserByUsername(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                return user;
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Get user by username error: " + e.getMessage());
            return null;
        } finally {
            closeResources(rs, stmt);
        }
    }
    
    public boolean updateUserPassword(String username, String newHashedPassword) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "UPDATE users SET password_hash = ? WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newHashedPassword);
            stmt.setString(2, username);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Update password error: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, stmt);
        }
    }
    
    private void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
    
    public void closeAllResources(ResultSet rs, Statement stmt, Connection conn) {
        closeResources(rs, stmt);
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

	public User getUserById(int userId) {
		// TODO Auto-generated method stub
		return null;
	}
}