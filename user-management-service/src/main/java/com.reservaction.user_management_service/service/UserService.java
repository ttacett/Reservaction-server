package com.reservaction.user_management_service.service;


import com.reservaction.user_management_service.dto.UserResponse;
import com.reservaction.user_management_service.entity.AppUser;
import com.reservaction.user_management_service.entity.StripeOnboardingStatus;
import com.reservaction.user_management_service.entity.UserRole;
import com.reservaction.user_management_service.repository.RoleRepository;
import com.reservaction.user_management_service.repository.UserRepository;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;

import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
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


    public String connectStripeAccount(String userId) throws Exception {
        Stripe.apiKey = "sk_test_51QNFpAGyn2SQYaBfihz3dOWtFpwRWtvP4UDSe63TQEiJKwtCrPphBZeoR4jJfD4lboPKabpVSLyxomHbP8vuPdEb00Or2dylJG";
        // Fetch user by ID //
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user has Stripe acc linked //
        if (user.getStripeOnboardingStatus() != null &&
                StripeOnboardingStatus.COMPLETED.equals(user.getStripeOnboardingStatus())) {
            throw new RuntimeException("Stripe account already fully linked.");
        }

        if (user.getOrganizerStripeAccount() != null) {
            throw new RuntimeException("Stripe account already linked for this user.");
        }

        // Create Stripe acc //
        Account account = createStripeAccount(user);

        // Update Stripe acc id and onboarding status for user //
        user.setOrganizerStripeAccount(account.getId());
        user.setStripeOnboardingStatus(StripeOnboardingStatus.PENDING);
        userRepository.save(user);

        // Generate onboarding Stripe link by passing user and acc ID //
        String paymentUrl = createAccountLink(user, account.getId());

        return paymentUrl;
    }


    private Account createStripeAccount(AppUser user) throws Exception {
        Stripe.apiKey = "sk_test_51QNFpAGyn2SQYaBfihz3dOWtFpwRWtvP4UDSe63TQEiJKwtCrPphBZeoR4jJfD4lboPKabpVSLyxomHbP8vuPdEb00Or2dylJG";
        return Account.create(AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .setEmail(user.getEmail())
                .setCountry("US")
                .setCapabilities(AccountCreateParams.Capabilities.builder()
                        .setTransfers(AccountCreateParams.Capabilities.Transfers.builder()
                                .setRequested(true)
                                .build())
                        .build())
                .build());
    }

    public String createAccountLink(AppUser user, String accountId) throws StripeException {
        Stripe.apiKey = "sk_test_51QNFpAGyn2SQYaBfihz3dOWtFpwRWtvP4UDSe63TQEiJKwtCrPphBZeoR4jJfD4lboPKabpVSLyxomHbP8vuPdEb00Or2dylJG";
        String refreshUrl = "http://localhost:8888/user-management-service/api/v1/stripe/onboard/refresh?userId=" + user.getId();
        String returnUrl = "http://localhost:8888/user-management-service/api/v1/stripe/onboard/return?accountId=" + user.getOrganizerStripeAccount();

        AccountLink accountLink = AccountLink.create(AccountLinkCreateParams.builder()
                .setAccount(accountId)
                .setRefreshUrl(refreshUrl)
                .setReturnUrl(returnUrl)
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build());

        return accountLink.getUrl();
    }
    public void markStripeOnboardingComplete(String accountId) {
        Stripe.apiKey = "sk_test_51QNFpAGyn2SQYaBfihz3dOWtFpwRWtvP4UDSe63TQEiJKwtCrPphBZeoR4jJfD4lboPKabpVSLyxomHbP8vuPdEb00Or2dylJG";
        AppUser user = userRepository.findByOrganizerStripeAccount(accountId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStripeOnboardingStatus(StripeOnboardingStatus.COMPLETED);
        userRepository.save(user);
    }

    public String getOrganizerStripeAccount(String organizerId) {
        AppUser user = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        if (user.getOrganizerStripeAccount() == null) {
            throw new RuntimeException("Organizer does not have a linked Stripe account.");
        }
        return user.getOrganizerStripeAccount();
    }


}
