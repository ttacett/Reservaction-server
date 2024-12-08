package com.reservaction.event_service.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EventRequest {

    private String title;
    private String category;
    private String address;
    private String city;
    private String dateTime; // Expecting "yyyy-MM-dd HH:mm"
    private Integer numberOfTickets;
    private Double ticketUnitPrice;
    private String description;
    private MultipartFile image;
}
