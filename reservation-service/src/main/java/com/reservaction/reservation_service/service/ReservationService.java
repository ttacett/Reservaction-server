package com.reservaction.reservation_service.service;

import com.reservaction.reservation_service.client.EventServiceClient;
import com.reservaction.reservation_service.dto.ReservationRequest;
import com.reservaction.reservation_service.dto.ReservationResponse;
import com.reservaction.reservation_service.entity.Reservation;
import com.reservaction.reservation_service.repository.ReservationRepository;
import com.reservaction.reservation_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EventServiceClient eventServiceClient;

    public ReservationResponse reserveEvent(ReservationRequest request) {
        String attendeeId = JwtUtil.getUserIdFromJwt();

        EventServiceClient.EventResponse event = eventServiceClient.getEventById(request.getEventId());

        if (event.getNumberOfTickets() < request.getNumberOfTickets()) {
            throw new RuntimeException("Not enough tickets available.");
        }

        double totalPrice = event.getTicketUnitPrice() * request.getNumberOfTickets();

        Reservation reservation = Reservation.builder()
                .attendeeId(attendeeId)
                .eventId(event.getId())
                .numberOfTickets(request.getNumberOfTickets())
                .totalPrice(totalPrice)
                .reservationDate(LocalDateTime.now())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);


        return mapToResponse(savedReservation, event);
    }

    private ReservationResponse mapToResponse(Reservation reservation, EventServiceClient.EventResponse event) {
        ReservationResponse response = new ReservationResponse();
        response.setReservationId(reservation.getId());
        response.setEventId(reservation.getEventId());
        response.setAttendeeId(reservation.getAttendeeId());
        response.setNumberOfTickets(reservation.getNumberOfTickets());
        response.setReservationDate(reservation.getReservationDate());
        response.setEventTitle(event.getTitle());
        response.setTicketUnitPrice(event.getTicketUnitPrice());
        response.setTotalPrice(reservation.getTotalPrice());
        return response;
    }
}

