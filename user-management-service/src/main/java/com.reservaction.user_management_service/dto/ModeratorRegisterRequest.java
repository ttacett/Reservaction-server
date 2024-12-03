package com.reservaction.user_management_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ModeratorRegisterRequest {
    private String username;
    private String password;
    private List<String> roles;
}
