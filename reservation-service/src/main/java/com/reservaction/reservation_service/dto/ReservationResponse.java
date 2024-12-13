package com.reservaction.reservation_service.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationResponse {
    private Long reservationId;
    private Long eventId;
    private String attendeeId;
    private int numberOfTickets;
    private double ticketUnitPrice;
    private double totalPrice;
    private LocalDateTime reservationDate;
    private String eventTitle;
}

