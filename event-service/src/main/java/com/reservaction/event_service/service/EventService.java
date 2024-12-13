package com.reservaction.event_service.service;

import com.reservaction.event_service.dto.EventRequest;
import com.reservaction.event_service.dto.EventResponse;
import com.reservaction.event_service.entity.Event;
import com.reservaction.event_service.entity.ImageData;
import com.reservaction.event_service.repository.EventRepository;
import com.reservaction.event_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ImageService imageService;

    // Create event //
    public EventResponse createEvent(EventRequest eventRequest) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(eventRequest.getDateTime(), formatter);
        String organizerId = JwtUtil.getUserIdFromJwt();

        byte[] imageBytes = eventRequest.getImage().getBytes();
        byte[] compressedImage = imageService.compress(imageBytes);
        byte[] decompressedImage = imageService.decompress(compressedImage);

        ImageData imageEntity = new ImageData();
        imageEntity.setCompressedData(compressedImage);
        imageEntity.setDecompressedData(decompressedImage);
        imageEntity.setName(eventRequest.getImage().getOriginalFilename());
        imageEntity.setType(eventRequest.getImage().getContentType());

        Event event = Event.builder()
                .title(eventRequest.getTitle())
                .category(eventRequest.getCategory())
                .address(eventRequest.getAddress())
                .city(eventRequest.getCity())
                .eventDateTime(dateTime)
                .numberOfTickets(eventRequest.getNumberOfTickets())
                .ticketUnitPrice(eventRequest.getTicketUnitPrice())
                .description(eventRequest.getDescription())
                .image(imageEntity)
                .organizerId(organizerId)
                .build();

        Event savedEvent = eventRepository.save(event);

        String base64Image = Base64.getEncoder().encodeToString(decompressedImage);
        return mapToResponse(savedEvent, base64Image);
    }

    // Get Event by ID //
    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        String base64Image = Base64.getEncoder().encodeToString(event.getImage().getDecompressedData());
        return mapToResponse(event, base64Image);
    }

    // Get All Events //
    public List<EventResponse> getAllEvents() {
        List<Event> events = eventRepository.findAll();

        return events.stream().map(event -> {
            String base64Image = Base64.getEncoder().encodeToString(event.getImage().getDecompressedData());
            return mapToResponse(event, base64Image);
        }).collect(Collectors.toList());
    }

    // Update Event //
    public EventResponse updateEvent(Long eventId, EventRequest eventRequest) throws Exception {

        String loggedInUserId = JwtUtil.getUserIdFromJwt();

        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!existingEvent.getOrganizerId().equals(loggedInUserId)) {
            throw new RuntimeException("You are not authorized to access this event.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(eventRequest.getDateTime(), formatter);

        existingEvent.setTitle(eventRequest.getTitle());
        existingEvent.setCategory(eventRequest.getCategory());
        existingEvent.setAddress(eventRequest.getAddress());
        existingEvent.setCity(eventRequest.getCity());
        existingEvent.setEventDateTime(dateTime);
        existingEvent.setNumberOfTickets(eventRequest.getNumberOfTickets());
        existingEvent.setTicketUnitPrice(eventRequest.getTicketUnitPrice());
        existingEvent.setDescription(eventRequest.getDescription());

        if (eventRequest.getImage() != null) {
            byte[] imageBytes = eventRequest.getImage().getBytes();
            byte[] compressedImage = imageService.compress(imageBytes);
            byte[] decompressedImage = imageService.decompress(compressedImage);

            ImageData imageEntity = existingEvent.getImage();
            imageEntity.setCompressedData(compressedImage);
            imageEntity.setDecompressedData(decompressedImage);
            imageEntity.setName(eventRequest.getImageName());
            imageEntity.setType(eventRequest.getImageType());

            existingEvent.setImage(imageEntity);
        }

        Event updatedEvent = eventRepository.save(existingEvent);

        String base64Image = Base64.getEncoder().encodeToString(existingEvent.getImage().getDecompressedData());
        return mapToResponse(updatedEvent, base64Image);
    }


    // Delete Event //
    public void deleteEvent(Long eventId) {
        String loggedInUserId = JwtUtil.getUserIdFromJwt();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOrganizerId().equals(loggedInUserId)) {
            throw new RuntimeException("You are not authorized to access this event.");
        }
        eventRepository.deleteById(eventId);
    }

    //allow organizer to access their event //
//    public EventResponse getEventById(Long eventId) {
//        String loggedInUserId = JwtUtil.getUserIdFromJwt();
//
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new RuntimeException("Event not found"));
//
//        if (!event.getOrganizerId().equals(loggedInUserId)) {
//            throw new RuntimeException("You are not authorized to view this event.");
//        }
//
//        String base64Image = Base64.getEncoder().encodeToString(event.getImage().getDecompressedData());
//        return mapToResponse(event, base64Image);
//    }

    // Get events for logged organizer only //
//    public List<EventResponse> getAllEvents() {
//        String loggedInUserId = JwtUtil.getUserIdFromJwt();
//
//        List<Event> events = eventRepository.findAll().stream()
//                .filter(event -> event.getOrganizerId().equals(loggedInUserId))
//                .toList();
//
//        return events.stream().map(event -> {
//            String base64Image = Base64.getEncoder().encodeToString(event.getImage().getDecompressedData());
//            return mapToResponse(event, base64Image);
//        }).collect(Collectors.toList());
//    }

    // Map Event to EventResponse //
    private EventResponse mapToResponse(Event event, String base64Image) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setCategory(event.getCategory());
        response.setAddress(event.getAddress());
        response.setCity(event.getCity());
        response.setEventDateTime(event.getEventDateTime());
        response.setNumberOfTickets(event.getNumberOfTickets());
        response.setTicketUnitPrice(event.getTicketUnitPrice());
        response.setDescription(event.getDescription());
        response.setImageName(event.getImage().getName());
        response.setImageType(event.getImage().getType());
        response.setImageBase64(base64Image);
        response.setOrganizerId(event.getOrganizerId());
        return response;
    }

}
