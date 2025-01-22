package com.reservaction.reservation_service.repository;

import com.reservaction.reservation_service.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT SUM(r.numberOfTickets) FROM Reservation r WHERE r.eventId = :eventId")
    Integer getReservedTicketsForEvent(@Param("eventId") Long eventId);
}
