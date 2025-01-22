package com.reservaction.event_service.client;


import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@FeignClient(name = "reservation-service")
public interface ReservationServiceClient {

    @GetMapping("/api/v1/reservations/total/{eventId}")
    Integer getReservedTicketsForEvent(@PathVariable("eventId") Long eventId);

    @GetMapping("/api/v1/reservations/{reservationId}")
    ReservationResponse getReservationDetails(@PathVariable Long reservationId);

    @PostMapping("/api/v1/reservations/{reservationId}/confirm")
    void confirmReservation(@PathVariable Long reservationId);

    @Data
    class ReservationResponse {
        private Long reservationId;
        private Long eventId;
        private String attendeeId;
        private int numberOfTickets;
        private double ticketUnitPrice;
        private double totalPrice;
        private LocalDateTime reservationDate;
        private String eventTitle;
        private ReservationStatus status;
    }
    public enum ReservationStatus {
        PENDING,
        COMPLETED
    }
    @Data
    public class ReservationRequest {
        private Long eventId;
        private int numberOfTickets;
    }
}

