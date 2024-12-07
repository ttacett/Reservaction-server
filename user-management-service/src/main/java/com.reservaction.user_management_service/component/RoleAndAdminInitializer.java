package com.reservaction.user_management_service.component;

import com.reservaction.user_management_service.entity.AppUser;
import com.reservaction.user_management_service.entity.UserRole;
import com.reservaction.user_management_service.repository.RoleRepository;
import com.reservaction.user_management_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleAndAdminInitializer implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        initializeRole("ADMIN");
        initializeRole("MODERATOR");
        initializeRole("ORGANIZER");
        initializeRole("USER");

        initializeAdminUser();
    }

    private void initializeRole(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            UserRole role = new UserRole(null, roleName);
            roleRepository.save(role);
        }
    }

    private void initializeAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);
            admin.setApproved(true);
            admin.setRoles(List.of(
                    roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"))
            ));
            userRepository.save(admin);
        }
    }
}
