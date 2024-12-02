package com.reservaction.user_management_service.service;


import com.reservaction.user_management_service.entity.AppUser;
import com.reservaction.user_management_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
}
