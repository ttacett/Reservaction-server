package com.reservaction.user_management_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class AppUser {

    @Id
    private String Id;
    private String username;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @DBRef
    private List<UserRole> roles = new ArrayList<>();

    // acc verification //
    private String verificationToken;
    private boolean isEnabled;

    // forgot password //
    private String resetPasswordToken;
    private Instant resetPasswordTokenExpiry;

    // supplement for organizers //
    private String organization;
    private boolean isApproved;
}
