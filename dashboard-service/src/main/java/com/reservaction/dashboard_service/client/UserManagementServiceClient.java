package com.reservaction.dashboard_service.client;

import com.reservaction.dashboard_service.security.FeignClientConfig;
import com.reservaction.user_management_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "user-management-service", configuration = FeignClientConfig.class)
public interface UserManagementServiceClient {

    @GetMapping("/api/v1/users/organizers")
    List<UserResponse> getOrganizers();

    @GetMapping("/api/v1/users/attendees")
    List<UserResponse> getAttendees();

    @GetMapping("/api/v1/users/moderators")
    List<UserResponse> getModerators();

    @GetMapping("/api/v1/users/{id}")
    UserResponse getUserById(@PathVariable("id") String id);
}
