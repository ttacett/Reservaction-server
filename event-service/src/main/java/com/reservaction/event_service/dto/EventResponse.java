package com.reservaction.event_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class EventResponse {
    private Long id;
    private String title;
    private String category;
    private String address;
    private String city;
    private LocalDateTime eventDateTime;
    private int numberOfTickets;
    private double ticketUnitPrice;
    private String description;
    private String imageName;
    private String imageType;
    private String imageBase64;
    private String organizerId;
}
