package com.reservaction.reservation_service.dto;


import com.reservaction.reservation_service.entity.Reservation;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class ReservationResponse {
    private Long reservationId;
    private Long eventId;
    private String attendeeId;
    private int numberOfTickets;
    private double ticketUnitPrice;
    private double totalPrice;
    private LocalDateTime reservationDate;
    private String eventTitle;
    private Reservation.ReservationStatus status;
    private int remainingTickets;
}

