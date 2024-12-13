package com.reservaction.event_service.controller;

import com.reservaction.event_service.dto.EventRequest;
import com.reservaction.event_service.dto.EventResponse;
import com.reservaction.event_service.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@PreAuthorize("hasAuthority('SCOPE_ORGANIZER')")
public class EventController {

    @Autowired
    private EventService eventService;

    // create event //
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createEvent(@ModelAttribute EventRequest eventRequest) {
        try {
            EventResponse event = eventService.createEvent(eventRequest);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // get all events //
    @GetMapping
    public ResponseEntity<?> getAllEvents() {
        try {
            List<EventResponse> events = eventService.getAllEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // get event by ID //
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            EventResponse event = eventService.getEventById(id);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // update event //
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @ModelAttribute EventRequest eventRequest
    ) {
        try {
            EventResponse updatedEvent = eventService.updateEvent(id, eventRequest);
            return ResponseEntity.ok(updatedEvent);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // delete event //
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.ok("Event deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}

