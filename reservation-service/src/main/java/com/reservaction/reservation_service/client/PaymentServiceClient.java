package com.reservaction.reservation_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/payments/create-session")
    String createStripeSession(@RequestParam("reservationId") Long reservationId);

    //@PostMapping("/api/payments/success")
    //void notifyPaymentSuccess(@RequestParam("reservationId") Long reservationId);
}

