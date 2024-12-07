package com.reservaction.user_management_service.dto;

import com.reservaction.user_management_service.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String id;
    private String username;
    private String email;
    private List<UserRole> roles;
    private String status;
    private String organization;
    private String approvalStatus;
    private Instant creationDate;
}
