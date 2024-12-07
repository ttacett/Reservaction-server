package com.reservaction.user_management_service.service;


import com.reservaction.user_management_service.dto.UserResponse;
import com.reservaction.user_management_service.entity.AppUser;
import com.reservaction.user_management_service.entity.UserRole;
import com.reservaction.user_management_service.repository.RoleRepository;
import com.reservaction.user_management_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public List<AppUser> getAllUsers(){
        return userRepository.findAll();
    }

    public Optional<AppUser> getUserById(String id){
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found" + id)));
    }

    public Optional<AppUser> getUserByUsername(String username){
        return Optional.ofNullable(userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("User not found" + username)));
    }

    public Optional<AppUser> getUserByEmail(String email){
        return Optional.ofNullable(userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found" + email)));
    }

    public List<UserResponse> getUsersByRole(String roleName) {

        Optional<UserRole> roleOptional = roleRepository.findByName(roleName);

        if (roleOptional.isEmpty()) {
            return Collections.emptyList();
        }
        // fetching users w role id //
        List<AppUser> users = userRepository.findByRoles_Id(roleOptional.get().getId());


        return users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRoles(),
                        user.isEnabled() ? "Active" : "Disabled", // Map to "Active" or "Disabled"
                        user.getOrganization(),
                        "ORGANIZER".equalsIgnoreCase(roleName) && user.isApproved() ? "Approved" : "Pending", // Check isApproved for ORGANIZER
                        user.getCreationDate()
                ))
                .collect(Collectors.toList());
    }

}
