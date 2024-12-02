package com.reservaction.user_management_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mailing-service")
public interface EmailServiceClient {

    @PostMapping("/send-reset-password-email")
    void sendResetPasswordEmail(@RequestBody String email, @RequestBody String token);

    @PostMapping("/send-verification-email")
    void sendVerificationEmail(@RequestBody String email, @RequestBody String token);
}
