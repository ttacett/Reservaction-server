package com.reservaction.dashboard_service.controller;

import com.reservaction.dashboard_service.client.UserManagementServiceClient;
import com.reservaction.user_management_service.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    @Autowired
    private UserManagementServiceClient userManagementServiceClient;

    @GetMapping("/organizers")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MODERATOR')")
    public ResponseEntity<List<UserResponse>> getOrganizers() {
        List<UserResponse> organizers = userManagementServiceClient.getOrganizers();
        return ResponseEntity.ok(organizers);
    }

    @GetMapping("/attendees")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MODERATOR')")
    public ResponseEntity<List<UserResponse>> getAttendees() {
        List<UserResponse> attendees = userManagementServiceClient.getAttendees();
        return ResponseEntity.ok(attendees);
    }

    @GetMapping("/moderators")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getModerators() {
        List<UserResponse> moderators = userManagementServiceClient.getModerators();
        return ResponseEntity.ok(moderators);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MODERATOR')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id){
        UserResponse users = userManagementServiceClient.getUserById(id);
        return ResponseEntity.ok(users);
    }
}
