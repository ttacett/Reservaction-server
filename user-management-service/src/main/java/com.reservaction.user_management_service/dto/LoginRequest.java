package com.reservaction.user_management_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {
    private String username;
    private String password;
    private boolean rememberMe;
}
