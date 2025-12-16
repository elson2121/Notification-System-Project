package com.notification.engine;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    
    // Configuration - Update these with your email credentials
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String USERNAME = "your-email@gmail.com"; // CHANGE THIS
    private static final String PASSWORD = "your-app-password"; // CHANGE THIS (use App Password, not regular password)
    private static final String FROM_EMAIL = "your-email@gmail.com"; // CHANGE THIS
    
    public boolean sendEmail(String toEmail, String subject, String body) {
        // If no email is configured, just log and return true for testing
        if (USERNAME.equals("your-email@gmail.com")) {
            System.out.println("📧 TEST MODE: Would send email to: " + toEmail);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            return true; // Return true for testing
        }
        
        try {
            // Set up mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            
            // Create session with authentication
            Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });
            
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            
            // Send message
            Transport.send(message);
            
            System.out.println("✅ Email sent successfully to: " + toEmail);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email to " + toEmail);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Alternative: Send HTML email
    public boolean sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        // If no email is configured, just log and return true for testing
        if (USERNAME.equals("your-email@gmail.com")) {
            System.out.println("📧 TEST MODE: Would send HTML email to: " + toEmail);
            System.out.println("Subject: " + subject);
            System.out.println("HTML Body: " + htmlBody);
            return true; // Return true for testing
        }
        
        try {
            // Set up mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            
            // Create session with authentication
            Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });
            
            // Create email message with HTML content
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            
            // Set HTML content
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html; charset=utf-8");
            
            // Create multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);
            
            message.setContent(multipart);
            
            // Send message
            Transport.send(message);
            
            System.out.println("✅ HTML email sent successfully to: " + toEmail);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send HTML email to " + toEmail);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error sending HTML email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Optional: Method to send email with attachments
    public boolean sendEmailWithAttachment(String toEmail, String subject, String body, String attachmentPath) {
        // If no email is configured, just log and return true for testing
        if (USERNAME.equals("your-email@gmail.com")) {
            System.out.println("📧 TEST MODE: Would send email with attachment to: " + toEmail);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            System.out.println("Attachment: " + attachmentPath);
            return true; // Return true for testing
        }
        
        try {
            // Set up mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            
            // Create session with authentication
            Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });
            
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            
            // Create multipart message
            Multipart multipart = new MimeMultipart();
            
            // Add text body
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(body);
            multipart.addBodyPart(textBodyPart);
            
            // Add attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            javax.activation.DataSource source = new javax.activation.FileDataSource(attachmentPath);
            attachmentPart.setDataHandler(new javax.activation.DataHandler(source));
            attachmentPart.setFileName(new java.io.File(attachmentPath).getName());
            multipart.addBodyPart(attachmentPart);
            
            message.setContent(multipart);
            
            // Send message
            Transport.send(message);
            
            System.out.println("✅ Email with attachment sent successfully to: " + toEmail);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email with attachment to " + toEmail);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error sending email with attachment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}