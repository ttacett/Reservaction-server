package com.reservaction.user_management_service.service;


import com.reservaction.user_management_service.client.EmailServiceClient;
import com.reservaction.user_management_service.dto.EmailRequest;
import com.reservaction.user_management_service.entity.AppUser;
import com.reservaction.user_management_service.entity.UserRole;
import com.reservaction.user_management_service.repository.RoleRepository;
import com.reservaction.user_management_service.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class RegisterService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailServiceClient emailServiceClient;



    // registering attendees //
    public AppUser registerUser(String username, String email, String password, List<String> roleNames) throws IllegalAccessException {
        if (userRepository.findByUsername(username).isPresent()){
            throw new IllegalAccessException("Username taken");
        }
        String hashedPassword = passwordEncoder.encode(password);

        List<UserRole> roles;
        if (roleNames == null || roleNames.isEmpty()) {
            roles = List.of(roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("role not found")));
        } else {
            roles = roleNames.stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toList());
        }

        String token = UUID.randomUUID().toString();

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRoles(roles);
        user.setVerificationToken(token);
        user.setEnabled(false);
        user.setApproved(true);
        user.setCreationDate(Instant.now());

        AppUser registeredUser = userRepository.save(user);
        sendVerificationEmail(email, token);
        return registeredUser;
    }

    // registering organizers //
    public AppUser registerOrganizer(String organization, String username, String email, String password, List<String> roleNames) throws IllegalAccessException {
        if (userRepository.findByUsername(username).isPresent()){
            throw new IllegalAccessException("Username taken");
        }
        String hashedPassword = passwordEncoder.encode(password);

        List<UserRole> roles;
        if (roleNames == null || roleNames.isEmpty()) {
            roles = List.of(roleRepository.findByName("ORGANIZER")
                    .orElseThrow(() -> new RuntimeException("role not found")));
        } else {
            roles = roleNames.stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toList());
        }

        String token = UUID.randomUUID().toString();

        AppUser user = new AppUser();
        user.setOrganization(organization);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRoles(roles);
        user.setVerificationToken(token);
        user.setEnabled(false);
        user.setApproved(false);
        user.setCreationDate(Instant.now());

        AppUser registeredOrganizer = userRepository.save(user);
        sendVerificationEmail(email, token);
        return registeredOrganizer;
    }

    // registering moderators //
    public AppUser registerModerator(String username, String password, List<String> roleNames) throws IllegalAccessException {
        if (userRepository.findByUsername(username).isPresent()){
            throw new IllegalAccessException("Username taken");
        }
        String hashedPassword = passwordEncoder.encode(password);

        List<UserRole> roles;
        if (roleNames == null || roleNames.isEmpty()) {
            roles = List.of(roleRepository.findByName("MODERATOR")
                    .orElseThrow(() -> new RuntimeException("role not found")));
        } else {
            roles = roleNames.stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toList());
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setRoles(roles);
        user.setEnabled(true);
        user.setCreationDate(Instant.now());

        return userRepository.save(user);
    }

    // sending verification email using mailing service //
    public void sendVerificationEmail(String email, String token){
        EmailRequest request = new EmailRequest();
        request.setEmail(email);
        request.setToken(token);
        emailServiceClient.sendVerificationEmail(request);
    }

    // verifying users with emails //
    public boolean verifyUser(String token) {
        Optional<AppUser> userOptional = userRepository.findByVerificationToken(token);
        if (userOptional.isEmpty()) return false;

        AppUser user = userOptional.get();
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        return true;
    }

    // approving organizers if they exist //
    public void approveOrganizer(String id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRoles().stream().anyMatch(role -> role.getName().equals("ORGANIZER"))) {
            throw new IllegalArgumentException("User is not an organizer");
        }

        if (user.isApproved()) {
            throw new IllegalStateException("Organizer is already approved");
        }

        user.setApproved(true);
        userRepository.save(user);
    }

    // disabling users account //
    public void disableUser(String id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new IllegalStateException("User is already disabled");
        }
        user.setEnabled(false);
        userRepository.save(user);
    }

    // enabling users account //
    public void enableUser(String id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEnabled()) {
            throw new IllegalStateException("User is already enabled");
        }
        user.setEnabled(true);
        userRepository.save(user);
    }
}
