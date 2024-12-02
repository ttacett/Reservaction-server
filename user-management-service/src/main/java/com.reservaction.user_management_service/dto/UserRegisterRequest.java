package com.reservaction.user_management_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UserRegisterRequest {
    private String username;
    private String email;
    private String password;
    private List<String> roles;
}
