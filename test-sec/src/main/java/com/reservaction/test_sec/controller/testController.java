package com.reservaction.test_sec.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
public class testController {

/*    @GetMapping
    public String getReservations(@RequestHeader("X-User-Id") String userId) {
        return "Reservations for user with ID: " + userId;
    }*/
    @GetMapping
    public String test() {
        return "Reservations for user with ID: ";
    }

}