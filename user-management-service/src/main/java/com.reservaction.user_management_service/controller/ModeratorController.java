package com.reservaction.user_management_service.controller;

import com.reservaction.user_management_service.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/moderator")
@PreAuthorize("hasAuthority('SCOPE_MODERATOR')")
public class ModeratorController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/approve-organizer")
    public ResponseEntity<String> approveOrganizer(@RequestParam String id) {
        try {
            registerService.approveOrganizer(id);
            return ResponseEntity.ok("Organizer approved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
