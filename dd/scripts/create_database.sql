-- ============================================
-- Instant Notification Engine Database Schema
-- Version: 1.0.0
-- Created: 2024
-- ============================================

-- Database creation
CREATE DATABASE IF NOT EXISTS notification_db;
USE notification_db;

-- ============================================
-- USERS TABLE (with login functionality)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'VIEWER',
    is_active BOOLEAN DEFAULT TRUE,
    department VARCHAR(100),
    avatar_url VARCHAR(255),
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- NOTIFICATIONS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS notifications (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    channel VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    sent_time TIMESTAMP NULL,
    delivered_time TIMESTAMP NULL,
    read_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- NOTIFICATION LOGS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS notification_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    notification_id VARCHAR(50) NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TEMPLATES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS templates (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    channel VARCHAR(20) NOT NULL,
    category VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- LOGIN HISTORY TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS login_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    success BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- ROLES TABLE (Optional - for advanced permission system)
-- ============================================
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    permissions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- USER ROLES TABLE (Many-to-Many relationship)
-- ============================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id VARCHAR(50) NOT NULL,
    role_id INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- INDEXES for better performance
-- ============================================

-- Users table indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(is_active);
CREATE INDEX idx_users_created ON users(created_at);

-- Notifications table indexes
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_sent_time ON notifications(sent_time);
CREATE INDEX idx_notifications_channel ON notifications(channel);

-- Notification logs indexes
CREATE INDEX idx_notification_logs_notification_id ON notification_logs(notification_id);
CREATE INDEX idx_notification_logs_user_id ON notification_logs(user_id);
CREATE INDEX idx_notification_logs_created_at ON notification_logs(created_at);

-- Login history indexes
CREATE INDEX idx_login_history_user_id ON login_history(user_id);
CREATE INDEX idx_login_history_login_time ON login_history(login_time);
CREATE INDEX idx_login_history_success ON login_history(success);

-- Templates indexes
CREATE INDEX idx_templates_name ON templates(name);
CREATE INDEX idx_templates_channel ON templates(channel);
CREATE INDEX idx_templates_active ON templates(is_active);

-- ============================================
-- DEFAULT DATA INSERTION
-- ============================================

-- Insert default roles
INSERT INTO roles (name, description, permissions) VALUES
('ADMIN', 'System Administrator', 'ALL_PERMISSIONS'),
('OPERATOR', 'Notification Operator', 'SEND_NOTIFICATION,VIEW_HISTORY,VIEW_STATS'),
('VIEWER', 'Data Viewer', 'VIEW_HISTORY,VIEW_STATS')
ON DUPLICATE KEY UPDATE description = VALUES(description), permissions = VALUES(permissions);

-- Insert default admin user (password: Admin@123)
INSERT INTO users (id, username, password_hash, full_name, email, phone, role, is_active) 
VALUES (
    'admin_001',
    'admin',
    '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', -- SHA-256 hash of 'Admin@123'
    'System Administrator',
    'admin@notification.com',
    '+251911223344',
    'ADMIN',
    TRUE
) ON DUPLICATE KEY UPDATE 
    full_name = VALUES(full_name),
    email = VALUES(email),
    phone = VALUES(phone),
    updated_at = CURRENT_TIMESTAMP;

-- Insert default operator user (password: Operator@123)
INSERT INTO users (id, username, password_hash, full_name, email, phone, role, is_active) 
VALUES (
    'operator_001',
    'operator',
    '4e6b4e6f5e7d6c5b4a3b2c1d0e9f8a7b6c5d4e3f2g1h0i9j8k7l6m5n4o3p2q1r0', -- Example hash
    'System Operator',
    'operator@notification.com',
    '+251922334455',
    'OPERATOR',
    TRUE
) ON DUPLICATE KEY UPDATE 
    full_name = VALUES(full_name),
    email = VALUES(email),
    phone = VALUES(phone),
    updated_at = CURRENT_TIMESTAMP;

-- Insert default viewer user (password: Viewer@123)
INSERT INTO users (id, username, password_hash, full_name, email, phone, role, is_active) 
VALUES (
    'viewer_001',
    'viewer',
    '5f7a8b9c0d1e2f3g4h5i6j7k8l9m0n1o2p3q4r5s6t7u8v9w0x1y2z3a4b5c6d7e8f', -- Example hash
    'System Viewer',
    'viewer@notification.com',
    '+251933445566',
    'VIEWER',
    TRUE
) ON DUPLICATE KEY UPDATE 
    full_name = VALUES(full_name),
    email = VALUES(email),
    phone = VALUES(phone),
    updated_at = CURRENT_TIMESTAMP;

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.role = r.name 
ON DUPLICATE KEY UPDATE assigned_at = CURRENT_TIMESTAMP;

-- Insert sample notification templates
INSERT INTO templates (id, name, content, channel, category, is_active) VALUES
('welcome_sms', 'Welcome SMS', 'Welcome {name} to Notification Engine! Your account is now active.', 'SMS', 'WELCOME', TRUE),
('order_confirmation', 'Order Confirmation', 'Dear {name}, your order #{order_number} has been confirmed.', 'SMS', 'ORDER', TRUE),
('password_reset', 'Password Reset', 'Your password reset code is: {code}. Valid for 10 minutes.', 'SMS', 'SECURITY', TRUE),
('newsletter_email', 'Monthly Newsletter', '<html><body><h1>Newsletter</h1><p>Hello {name},</p><p>Here is our monthly update...</p></body></html>', 'EMAIL', 'NEWSLETTER', TRUE)
ON DUPLICATE KEY UPDATE 
    content = VALUES(content),
    updated_at = CURRENT_TIMESTAMP;

-- ============================================
-- STORED PROCEDURES (Optional)
-- ============================================

DELIMITER $$

-- Procedure to log user login
CREATE PROCEDURE IF NOT EXISTS log_user_login(
    IN p_user_id VARCHAR(50),
    IN p_ip_address VARCHAR(45),
    IN p_user_agent TEXT,
    IN p_success BOOLEAN
)
BEGIN
    INSERT INTO login_history (user_id, ip_address, user_agent, success)
    VALUES (p_user_id, p_ip_address, p_user_agent, p_success);
    
    IF p_success THEN
        UPDATE users 
        SET last_login = CURRENT_TIMESTAMP 
        WHERE id = p_user_id;
    END IF;
END$$

-- Procedure to get user statistics
CREATE PROCEDURE IF NOT EXISTS get_user_stats(
    IN p_user_id VARCHAR(50)
)
BEGIN
    SELECT 
        u.username,
        u.full_name,
        u.role,
        u.last_login,
        COUNT(DISTINCT n.id) as total_notifications,
        COUNT(DISTINCT lh.id) as total_logins,
        MAX(lh.login_time) as last_login_time
    FROM users u
    LEFT JOIN notifications n ON u.id = n.user_id
    LEFT JOIN login_history lh ON u.id = lh.user_id AND lh.success = TRUE
    WHERE u.id = p_user_id
    GROUP BY u.id;
END$$

DELIMITER ;

-- ============================================
-- VIEWS for reporting
-- ============================================

-- View for user activity report
CREATE OR REPLACE VIEW user_activity_report AS
SELECT 
    u.username,
    u.full_name,
    u.role,
    u.last_login,
    COUNT(DISTINCT n.id) as notifications_sent,
    COUNT(DISTINCT lh.id) as successful_logins,
    MAX(lh.login_time) as last_successful_login
FROM users u
LEFT JOIN notifications n ON u.id = n.user_id
LEFT JOIN login_history lh ON u.id = lh.user_id AND lh.success = TRUE
GROUP BY u.id;

-- View for notification statistics
CREATE OR REPLACE VIEW notification_statistics AS
SELECT 
    DATE(sent_time) as date,
    channel,
    status,
    COUNT(*) as count
FROM notifications
WHERE sent_time IS NOT NULL
GROUP BY DATE(sent_time), channel, status;

-- ============================================
-- TRIGGERS for auditing
-- ============================================

-- Trigger to log user updates
DELIMITER $$
CREATE TRIGGER IF NOT EXISTS before_user_update
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    IF OLD.role != NEW.role THEN
        INSERT INTO notification_logs (user_id, action, details)
        VALUES (NEW.id, 'ROLE_CHANGED', CONCAT('Role changed from ', OLD.role, ' to ', NEW.role));
    END IF;
    
    IF OLD.is_active != NEW.is_active THEN
        INSERT INTO notification_logs (user_id, action, details)
        VALUES (NEW.id, 'STATUS_CHANGED', CONCAT('Account ', IF(NEW.is_active, 'activated', 'deactivated')));
    END IF;
END$$
DELIMITER ;

-- ============================================
-- FINAL MESSAGES
-- ============================================

SELECT 'Database created successfully!' as message;
SELECT 'Tables created:' as table_list;
SHOW TABLES;
SELECT 'Default users inserted:' as user_list;
SELECT username, full_name, role FROM users;