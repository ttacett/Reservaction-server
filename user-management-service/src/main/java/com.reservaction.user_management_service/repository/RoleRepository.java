package com.reservaction.user_management_service.repository;

import com.reservaction.user_management_service.entity.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<UserRole, String> {
    Optional<UserRole> findByName(String name);

}
