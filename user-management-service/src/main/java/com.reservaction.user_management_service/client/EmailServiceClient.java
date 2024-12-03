package com.reservaction.user_management_service.client;

import com.reservaction.user_management_service.dto.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mailing-service")
public interface EmailServiceClient {

    @PostMapping("/api/v1/email/send-reset-password-email")
    void sendResetPasswordEmail(@RequestBody EmailRequest request);

    @PostMapping("/api/v1/email/send-verification-email")
    void sendVerificationEmail(@RequestBody EmailRequest request);
}
