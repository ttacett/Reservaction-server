package com.reservaction.mailing_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailRequest {
    String email;
    String token;
}
