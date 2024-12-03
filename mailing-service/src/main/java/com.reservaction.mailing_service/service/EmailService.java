package com.reservaction.mailing_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    public void sendResetPasswordEmail(String email, String token){
        String subject = "Password reset request";
        String resetUrl = "http://localhost:8888/user-management-service/api/v1/auth/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText("Click the link to reset your password:" + resetUrl);

        mailSender.send(message);
    }

    public void sendVerificationEmail(String email, String token){
        String subject = "Email Verification";
        String verificationUrl = "http://localhost:8888/user-management-service/api/v1/auth/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText("Click the link to verify your account:" + verificationUrl);

        mailSender.send(message);
    }
}
