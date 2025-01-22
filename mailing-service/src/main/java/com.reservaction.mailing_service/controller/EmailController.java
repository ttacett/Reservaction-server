package com.reservaction.mailing_service.controller;

import com.reservaction.mailing_service.dto.EmailRequest;
import com.reservaction.mailing_service.dto.EmailReservation;
import com.reservaction.mailing_service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
@CrossOrigin
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-reset-password-email")
    public ResponseEntity<String> sendResetPasswordEmail(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendResetPasswordEmail(emailRequest.getEmail(), emailRequest.getToken());
            return ResponseEntity.ok("Email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        }
    }

    @PostMapping("/send-verification-email")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendVerificationEmail(emailRequest.getEmail(), emailRequest.getToken());
            return ResponseEntity.ok("Email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        }
    }

    @PostMapping("/send-reservation")
    public ResponseEntity<?> sendEmail(@RequestBody EmailReservation emailDetails) {
        try {
            emailService.sendReservationEmail(emailDetails.getEmail(), emailDetails.getSubject(), emailDetails.getBody());
            return ResponseEntity.ok("Email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email: " + e.getMessage());
        }
    }
}
