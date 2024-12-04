package com.reservaction.user_management_service.controller;

import com.reservaction.user_management_service.dto.LoginRequest;
import com.reservaction.user_management_service.dto.OrganizerRegisterRequest;
import com.reservaction.user_management_service.dto.UserRegisterRequest;
import com.reservaction.user_management_service.entity.AppUser;
import com.reservaction.user_management_service.service.LoginService;
import com.reservaction.user_management_service.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
public class AuthController {


    @Autowired
    private RegisterService registerService;
    @Autowired
    private LoginService loginService;


    @PostMapping("/register-user")
    public ResponseEntity<AppUser> registerUser(@RequestBody UserRegisterRequest request) throws IllegalAccessException {
        AppUser newUser = registerService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/register-organizer")
    public ResponseEntity<AppUser> registerOrganizer(@RequestBody OrganizerRegisterRequest request) throws IllegalAccessException {
        AppUser newUser = registerService.registerOrganizer(
                request.getOrganization(),
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token){
        boolean isVerified = registerService.verifyUser(token);
        if (isVerified) {
            return ResponseEntity.ok("Account verified successfully");
        }
        return ResponseEntity.badRequest().body("Invalid verification token");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request){
        Map<String, String> token = loginService.loginUser(
                request.getUsername(),
                request.getPassword(),
                request.isRememberMe()
        );
        return ResponseEntity.ok(token);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request){
        String email = request.get("email");
        loginService.generateResetPasswordToken(email);
        return ResponseEntity.ok("Password reset email sent successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody Map<String, String> request){
        String newPassword = request.get("newPassword");
        loginService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password updated successfully!");
    }

}
