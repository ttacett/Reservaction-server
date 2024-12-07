package com.reservaction.dashboard_service.client;

import com.reservaction.dashboard_service.security.FeignClientConfig;
import com.reservaction.user_management_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user-management-service", configuration = FeignClientConfig.class)
public interface UserManagementServiceClient {

    @GetMapping("/api/v1/users/organizers")
    List<UserResponse> getOrganizers();

    @GetMapping("/api/v1/users/attendees")
    List<UserResponse> getAttendees();

    @GetMapping("/api/v1/users/moderators")
    List<UserResponse> getModerators();
}
