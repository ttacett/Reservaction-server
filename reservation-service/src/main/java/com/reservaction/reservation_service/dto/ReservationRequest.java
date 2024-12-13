package com.reservaction.reservation_service.dto;


import lombok.Data;

@Data
public class ReservationRequest {
    private Long eventId;
    private int numberOfTickets;
}
