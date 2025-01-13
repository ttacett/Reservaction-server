package com.reservaction.payment_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String sessionId;
    private String sessionUrl;
    private Long reservationId;
    private double amount;
}
