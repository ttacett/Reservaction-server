package com.reservaction.user_management_service.controller;

import com.reservaction.user_management_service.dto.ModeratorRegisterRequest;
import com.reservaction.user_management_service.dto.UserRegisterRequest;
import com.reservaction.user_management_service.entity.AppUser;
import com.reservaction.user_management_service.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
public class AdminController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/register-moderator")
    public ResponseEntity<AppUser> registerModerator(@RequestBody ModeratorRegisterRequest request) throws IllegalAccessException {
        AppUser newUser = registerService.registerModerator(
                request.getUsername(),
                request.getPassword(),
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/approve-organizer")
    public ResponseEntity<String> approveOrganizer(@RequestParam String id) {
        try {
            registerService.approveOrganizer(id);
            return ResponseEntity.ok("Organizer approved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/disable-user")
    public ResponseEntity<String> disableUser(@RequestParam String id) {
        try {
            registerService.disableUser(id);
            return ResponseEntity.ok("User disabled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/enable-user")
    public ResponseEntity<String> enableUser(@RequestParam String id) {
        try {
            registerService.enableUser(id);
            return ResponseEntity.ok("User enabled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
