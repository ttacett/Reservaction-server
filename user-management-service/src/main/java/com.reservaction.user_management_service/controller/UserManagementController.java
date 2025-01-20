package com.reservaction.user_management_service.controller;

import com.reservaction.user_management_service.dto.ModeratorRegisterRequest;
import com.reservaction.user_management_service.dto.UserResponse;
import com.reservaction.user_management_service.entity.AppUser;
import com.reservaction.user_management_service.service.RegisterService;
import com.reservaction.user_management_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserManagementController {

    @Autowired
    private RegisterService registerService;
    @Autowired
    private UserService userService;


    // FOR DASHBOARD-SERVICE //
    @GetMapping("/organizers")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MODERATOR')")
    public ResponseEntity<List<UserResponse>> getOrganizers() {
        List<UserResponse> organizers = userService.getUsersByRole("ORGANIZER");
        return ResponseEntity.ok(organizers);
    }

    @GetMapping("/attendees")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MODERATOR')")
    public ResponseEntity<List<UserResponse>> getAttendees() {
        List<UserResponse> attendees = userService.getUsersByRole("USER");
        return ResponseEntity.ok(attendees);
    }

    @GetMapping("/moderators")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getModerators() {
        List<UserResponse> moderators = userService.getUsersByRole("MODERATOR");
        return ResponseEntity.ok(moderators);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MODERATOR')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/moderators")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<AppUser> registerModerator(@RequestBody ModeratorRegisterRequest request) throws IllegalAccessException {
        AppUser newUser = registerService.registerModerator(
                request.getUsername(),
                request.getPassword(),
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/organizers/approve")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MODERATOR')")
    public ResponseEntity<String> approveOrganizer(@RequestParam String id) {
        try {
            registerService.approveOrganizer(id);
            return ResponseEntity.ok("Organizer approved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/disable")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MODERATOR')")
    public ResponseEntity<String> disableUser(@RequestParam String id) {
        try {
            registerService.disableUser(id);
            return ResponseEntity.ok("User disabled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/enable")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MODERATOR')")
    public ResponseEntity<String> enableUser(@RequestParam String id) {
        try {
            registerService.enableUser(id);
            return ResponseEntity.ok("User enabled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
