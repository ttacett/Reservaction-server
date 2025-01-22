package com.reservaction.payment_service.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mailing-service")
public interface MailingServiceClient {

    @PostMapping("/api/v1/email/send-reservation")
    void sendEmail(@RequestBody EmailReservation emailDetails);

    @Data
    class EmailReservation {
        private String email;
        private String subject;
        private String body;
    }
}

