package com.reservaction.user_management_service.controller;

import com.reservaction.user_management_service.security.JwtUtil;
import com.reservaction.user_management_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stripe")
public class StripeController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('SCOPE_ORGANIZER')")
    @PostMapping("/connect-stripe")
    public ResponseEntity<String> connectStripeAccount() throws Exception {
        String userId = JwtUtil.getUserIdFromJwt();
        String accountLink = userService.connectStripeAccount(userId);
        return ResponseEntity.ok(accountLink);
    }

    @GetMapping("/onboard/refresh")
    public ResponseEntity<?> refreshStripeOnboarding() {
        try {
            String userId = JwtUtil.getUserIdFromJwt();
            String newOnboardingUrl = userService.connectStripeAccount(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Retry onboarding by visiting the link below.");
            response.put("onboarding_url", newOnboardingUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retry onboarding: " + e.getMessage());
        }
    }

    @GetMapping("/onboard/return")
    public ResponseEntity<?> handleOnboardingSuccess(@RequestParam String accountId) {
        try {
            userService.markStripeOnboardingComplete(accountId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Stripe onboarding completed successfully.");
            response.put("account_id", accountId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process return URL: " + e.getMessage());
        }
    }
}
