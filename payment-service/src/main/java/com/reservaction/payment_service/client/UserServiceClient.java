package com.reservaction.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="user-management-service")
public interface UserServiceClient {

    @GetMapping("api/v1/users/getStripeAccount/{organizerId}")
    String getStripeAccount(@PathVariable String organizerId);
}
