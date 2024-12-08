package com.reservaction.event_service.repository;

import com.reservaction.event_service.entity.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<ImageData, Long> {
    Optional<ImageData> findByName(String fileName);
}
