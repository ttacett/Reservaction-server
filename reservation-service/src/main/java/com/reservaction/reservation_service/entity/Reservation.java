package com.reservaction.reservation_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String attendeeId;

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private int numberOfTickets;

    @Column(nullable = false)
    private double totalPrice;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    public enum ReservationStatus {
        PENDING,
        COMPLETED
    }
}
