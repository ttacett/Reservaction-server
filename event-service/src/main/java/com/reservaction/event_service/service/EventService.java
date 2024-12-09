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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ImageService imageService;

    public Event createEvent(EventRequest eventRequest) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(eventRequest.getDateTime(), formatter);
        String organizerId = JwtUtil.getUserIdFromJwt();

        ImageData imageData = imageService.uploadImage(eventRequest.getImage());

        Event event = Event.builder()
                .title(eventRequest.getTitle())
                .category(eventRequest.getCategory())
                .address(eventRequest.getAddress())
                .city(eventRequest.getCity())
                .eventDateTime(dateTime)
                .numberOfTickets(eventRequest.getNumberOfTickets())
                .ticketUnitPrice(eventRequest.getTicketUnitPrice())
                .description(eventRequest.getDescription())
                .image(imageData)
                .organizerId(organizerId)
                .build();

        return eventRepository.save(event);
    }

//    public List<Event> getAllEvents() {
//        return eventRepository.findAll();
//    }


    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToEventResponse)
                .collect(Collectors.toList());
    }

    private EventResponse mapToEventResponse(Event event) {
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

        if (event.getImage() != null) {
            ImageData image = event.getImage();
            response.setImageName(image.getName());
            response.setImageType(image.getType());
            response.setImageBase64(Base64.getEncoder().encodeToString(imageService.downloadImage(image.getName())));
        }

        return response;
    }

    public Event getEventById(Long id) throws Exception {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            throw new Exception("Event not found.");
        }
        return event.get();
    }

    public Event updateEvent(Long id, EventRequest eventRequest) throws Exception {
        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isEmpty()) {
            throw new Exception("Event not found.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(eventRequest.getDateTime(), formatter);

        ImageData imageData = imageService.uploadImage(eventRequest.getImage());

        Event event = existingEvent.get();
        event.setTitle(eventRequest.getTitle());
        event.setCategory(eventRequest.getCategory());
        event.setAddress(eventRequest.getAddress());
        event.setCity(eventRequest.getCity());
        event.setEventDateTime(dateTime);
        event.setNumberOfTickets(eventRequest.getNumberOfTickets());
        event.setTicketUnitPrice(eventRequest.getTicketUnitPrice());
        event.setDescription(eventRequest.getDescription());
        event.setImage(imageData);

        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) throws Exception {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            throw new Exception("Event not found.");
        }
        eventRepository.deleteById(id);
    }
}
