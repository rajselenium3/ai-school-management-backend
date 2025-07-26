package com.eduai.schoolmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@eduai-school.com}")
    private String fromEmail;

    @Value("${app.name:EduAI School Management}")
    private String appName;

    public void sendLoginVerificationCode(String email, String verificationCode, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Login Verification Code - " + appName);
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Your login verification code is: %s\n\n" +
                "This code will expire in 5 minutes.\n\n" +
                "If you didn't request this code, please ignore this email.\n\n" +
                "Best regards,\n" +
                "%s Team",
                firstName, verificationCode, appName
            ));

            mailSender.send(message);
            log.info("Login verification code sent to: {}", email);

        } catch (Exception e) {
            log.error("Failed to send login verification code to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendPasswordResetCode(String email, String resetCode, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Password Reset Code - " + appName);
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Your password reset code is: %s\n\n" +
                "This code will expire in 15 minutes.\n\n" +
                "If you didn't request this reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "%s Team",
                firstName, resetCode, appName
            ));

            mailSender.send(message);
            log.info("Password reset code sent to: {}", email);

        } catch (Exception e) {
            log.error("Failed to send password reset code to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendPasswordResetConfirmation(String email, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Password Reset Successful - " + appName);
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Your password has been successfully reset.\n\n" +
                "If you didn't make this change, please contact support immediately.\n\n" +
                "Best regards,\n" +
                "%s Team",
                firstName, appName
            ));

            mailSender.send(message);
            log.info("Password reset confirmation sent to: {}", email);

        } catch (Exception e) {
            log.error("Failed to send password reset confirmation to {}: {}", email, e.getMessage());
            // Don't throw exception here as it's not critical
        }
    }

    public void sendWelcomeEmail(String email, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Welcome to " + appName);
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Welcome to %s! Your account has been successfully created.\n\n" +
                "You can now log in and start using the system.\n\n" +
                "Best regards,\n" +
                "%s Team",
                firstName, appName, appName
            ));

            mailSender.send(message);
            log.info("Welcome email sent to: {}", email);

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", email, e.getMessage());
            // Don't throw exception here as it's not critical
        }
    }

    public void sendEmailVerification(String email, String verificationCode, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Email Verification - " + appName);
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Please verify your email address using this code: %s\n\n" +
                "This code will expire in 10 minutes.\n\n" +
                "Best regards,\n" +
                "%s Team",
                firstName, verificationCode, appName
            ));

            mailSender.send(message);
            log.info("Email verification code sent to: {}", email);

        } catch (Exception e) {
            log.error("Failed to send email verification to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
