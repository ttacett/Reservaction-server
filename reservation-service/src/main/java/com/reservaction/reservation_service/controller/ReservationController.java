package com.reservaction.reservation_service.controller;

import com.reservaction.reservation_service.dto.ReservationRequest;
import com.reservaction.reservation_service.dto.ReservationResponse;
import com.reservaction.reservation_service.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // Reserve event //
    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> reserveEvent(@RequestBody ReservationRequest request) {
        try {
            ReservationResponse savedReservation = reservationService.reserveEvent(request);
            return new ResponseEntity<>(savedReservation, HttpStatus.CREATED);


        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Get reservation details //
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservationDetails(@PathVariable Long reservationId) {
        try {
            ReservationResponse reservation = reservationService.getReservationDetails(reservationId);
            if (reservation != null) {
                return new ResponseEntity<>(reservation, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


