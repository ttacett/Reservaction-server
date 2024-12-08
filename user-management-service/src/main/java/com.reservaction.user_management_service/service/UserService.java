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
import java.util.NoSuchElementException;
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

    public UserResponse getUserById(String id) {
        Optional<AppUser> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new NoSuchElementException("User not found");
        }

        AppUser user = userOptional.get();

        boolean isOrganizer = user.getRoles().stream()
                .anyMatch(role -> "ORGANIZER".equalsIgnoreCase(role.getName()));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.isEnabled() ? "Active" : "Disabled",
                user.getOrganization() != null ? user.getOrganization() : "N/A",
                isOrganizer && user.isApproved() ? "Approved" : "Pending",
                user.getCreationDate()
        );
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
                        user.isEnabled() ? "Active" : "Disabled",
                        user.getOrganization() != null ? user.getOrganization() : "N/A",
                        "ORGANIZER".equalsIgnoreCase(roleName) && user.isApproved() ? "Approved" : "Pending",
                        user.getCreationDate()
                ))
                .collect(Collectors.toList());
    }

}
