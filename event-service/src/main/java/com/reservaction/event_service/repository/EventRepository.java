package com.reservaction.event_service.repository;


import com.reservaction.event_service.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
