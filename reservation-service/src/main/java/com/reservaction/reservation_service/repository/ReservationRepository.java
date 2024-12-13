package com.reservaction.reservation_service.repository;

import com.reservaction.reservation_service.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
