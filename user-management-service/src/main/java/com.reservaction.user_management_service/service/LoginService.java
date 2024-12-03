package com.reservaction.user_management_service.service;


import com.reservaction.user_management_service.client.EmailServiceClient;
import com.reservaction.user_management_service.dto.EmailRequest;
import com.reservaction.user_management_service.entity.AppUser;
import com.reservaction.user_management_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailServiceClient emailServiceClient;

    public Map<String, String> loginUser(String username, String password, boolean rememberMe){

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            Map<String, String> token = new HashMap<>();
            Instant instant = Instant.now();
            String scope = authentication.getAuthorities().stream().map(auth->auth.getAuthority()).collect(Collectors.joining(" "));
            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .subject(authentication.getName())
                    .issuedAt(instant)
                    .expiresAt(instant.plus(rememberMe?3:1, ChronoUnit.MINUTES))
                    .issuer("security")
                    .claim("scope", scope)
                    .build();
            String JwtAccessToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
            token.put("accessToken", JwtAccessToken);
            return token;

        } catch (DisabledException e) {
            throw new RuntimeException("User account is disabled! Please verify your email to activate your account.");
        } catch (Exception e) {
            throw new RuntimeException("Bad credentials!");
        }
    }

    public void generateResetPasswordToken(String email){
        Optional<AppUser> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) throw new RuntimeException("User with email not found!");

        String token = UUID.randomUUID().toString();
        AppUser user = userOptional.get();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(Instant.now().plus(5, ChronoUnit.MINUTES));

        userRepository.save(user);
        sendResetPasswordEmail(user.getEmail(), token);
    }

    public Optional<AppUser> validateResetPasswordToken(String token){
        Optional<AppUser> userOptional = userRepository.findByResetPasswordToken(token);
        if(userOptional.isEmpty()) throw new RuntimeException("Invalid token");

        AppUser user = userOptional.get();
        if(user.getResetPasswordTokenExpiry().isBefore(Instant.now())) throw new RuntimeException("Token expired!");

        return Optional.of(user);
    }

    public void resetPassword(String token, String newPassword){
        Optional<AppUser> userOptional = validateResetPasswordToken(token);
        if(userOptional.isEmpty()) throw new RuntimeException("Something's wrong!");


        AppUser user = userOptional.get();
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepository.save(user);
    }

    public void sendResetPasswordEmail(String email, String token){
        EmailRequest request = new EmailRequest();
        request.setEmail(email);
        request.setToken(token);
        emailServiceClient.sendResetPasswordEmail(request);
    }
}
