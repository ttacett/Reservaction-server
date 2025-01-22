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

/*@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private EventServiceClient eventServiceClient;

    public Reservation reserveEvent(ReservationRequest request) {
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
                .status(Reservation.ReservationStatus.PENDING)
                .build();

        return reservationRepository.save(reservation);
    }
}*/





//@Service
//public class ReservationService {
//
//    @Autowired
//    private ReservationRepository reservationRepository;
//
//    @Autowired
//    private EventServiceClient eventServiceClient;
//
//    public ReservationResponse reserveEvent(ReservationRequest request) {
//
//        // Validate event & ticket availability //
//        EventServiceClient.EventResponse event = eventServiceClient.getEventById(request.getEventId());
//        if (event.getNumberOfTickets() < request.getNumberOfTickets()) {
//            throw new RuntimeException("Not enough tickets available.");
//        }
//
//        // Create a "PENDING" reservation //
//        Reservation reservation = Reservation.builder()
//                .attendeeId(JwtUtil.getUserIdFromJwt())
//                .eventId(event.getId())
//                .numberOfTickets(request.getNumberOfTickets())
//                .totalPrice(event.getTicketUnitPrice() * request.getNumberOfTickets())
//                .status(Reservation.ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//
//        Reservation savedReservation = reservationRepository.save(reservation);
//        return mapToResponse(savedReservation);
//    }
//
//    public ReservationResponse getReservationDetails(Long reservationId) {
//        Reservation reservation = reservationRepository.findById(reservationId)
//                .orElseThrow(() -> new RuntimeException("Reservation not found"));
//
//        return mapToResponse(reservation);
//    }
//
//    public void confirmReservation(Long reservationId) {
//        Reservation reservation = reservationRepository.findById(reservationId)
//                .orElseThrow(() -> new RuntimeException("Reservation not found"));
//
//        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
//        reservationRepository.save(reservation);
//    }
//
//    private ReservationResponse mapToResponse(Reservation reservation) {
//        // Retrieve event details //
//        EventServiceClient.EventResponse event = eventServiceClient.getEventById(reservation.getEventId());
//
//        return ReservationResponse.builder()
//                .reservationId(reservation.getId())
//                .eventId(reservation.getEventId())
//                .attendeeId(reservation.getAttendeeId())
//                .numberOfTickets(reservation.getNumberOfTickets())
//                .ticketUnitPrice(event.getTicketUnitPrice())
//                .eventTitle(event.getTitle())
//                .totalPrice(reservation.getTotalPrice())
//                .reservationDate(reservation.getReservationDate())
//                .status(reservation.getStatus())
//                .build();
//    }
//
//}


@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EventServiceClient eventServiceClient;

    public ReservationResponse reserveEvent(ReservationRequest request) {
        // Fetch event details from EventServiceClient
        EventServiceClient.EventResponse event = eventServiceClient.getEventById(request.getEventId());

        // Get the number of tickets already reserved for the event using the custom query
        Integer reservedTickets = reservationRepository.getReservedTicketsForEvent(request.getEventId());
        reservedTickets = (reservedTickets != null) ? reservedTickets : 0; // Default to 0 if no tickets are reserved

        // Calculate remaining tickets
        int remainingTickets = event.getNumberOfTickets() - reservedTickets;

        // Validate availability of tickets
        if (remainingTickets < request.getNumberOfTickets()) {
            throw new RuntimeException("Not enough tickets available. Only " + remainingTickets + " tickets left.");
        }

        // Create and save a pending reservation
        Reservation reservation = Reservation.builder()
                .attendeeId(JwtUtil.getUserIdFromJwt())
                .eventId(event.getId())
                .numberOfTickets(request.getNumberOfTickets())
                .totalPrice(event.getTicketUnitPrice() * request.getNumberOfTickets())
                .status(Reservation.ReservationStatus.PENDING)
                .reservationDate(LocalDateTime.now())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        // Return the mapped response including remaining tickets
        return mapToResponse(savedReservation, remainingTickets);
    }

    public ReservationResponse getReservationDetails(Long reservationId) {
        // Fetch reservation by ID and map to response
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

        return mapToResponse(reservation, null);  // remainingTickets not needed for this method
    }

    public void confirmReservation(Long reservationId) {
        // Fetch reservation by ID and change its status to COMPLETED
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
    }

    private ReservationResponse mapToResponse(Reservation reservation, Integer remainingTickets) {
        // Fetch event details once for mapping
        EventServiceClient.EventResponse event = eventServiceClient.getEventById(reservation.getEventId());

        // Use the passed remainingTickets, if available, or calculate it dynamically
        if (remainingTickets == null) {
            // Calculate remaining tickets if not already passed
            Integer reservedTickets = reservationRepository.getReservedTicketsForEvent(reservation.getEventId());
            remainingTickets = event.getNumberOfTickets() - (reservedTickets != null ? reservedTickets : 0);
        }

        // Build and return the response
        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .eventId(reservation.getEventId())
                .attendeeId(reservation.getAttendeeId())
                .numberOfTickets(reservation.getNumberOfTickets())
                .ticketUnitPrice(event.getTicketUnitPrice())
                .eventTitle(event.getTitle())
                .totalPrice(reservation.getTotalPrice())
                .reservationDate(reservation.getReservationDate())
                .status(reservation.getStatus())
                .remainingTickets(remainingTickets)
                .build();
    }
}





