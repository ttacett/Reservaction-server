package com.reservaction.mailing_service.dto;

import lombok.Data;

@Data
public class EmailReservation {
    private String email;
    private String subject;
    private String body;
}
