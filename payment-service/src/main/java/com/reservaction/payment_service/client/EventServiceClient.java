package com.reservaction.payment_service.client;

import com.reservaction.payment_service.security.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service", configuration = FeignClientConfig.class)
public interface EventServiceClient {

    @GetMapping("/api/v1/events/organizer/{id}")
    String getOrganizerByEvent(@PathVariable Long id);

}

