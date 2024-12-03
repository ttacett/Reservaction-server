package com.reservaction.user_management_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailRequest {
    private String email;
    private String token;
}
