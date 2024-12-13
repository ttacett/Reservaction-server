package com.reservaction.reservation_service.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="event-service")
public interface EventServiceClient {

    @GetMapping("/api/v1/events/{id}")
    EventResponse getEventById(@PathVariable("id") Long id);

    @Data @AllArgsConstructor @NoArgsConstructor
    class EventResponse {
        private long id;
        private String title;
        private int numberOfTickets;
        private double ticketUnitPrice;

    }
}
