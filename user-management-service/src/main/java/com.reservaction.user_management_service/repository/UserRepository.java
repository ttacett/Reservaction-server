package com.reservaction.user_management_service.repository;

import com.reservaction.user_management_service.entity.AppUser;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByVerificationToken(String token);
    Optional<AppUser> findByResetPasswordToken(String token);
}
