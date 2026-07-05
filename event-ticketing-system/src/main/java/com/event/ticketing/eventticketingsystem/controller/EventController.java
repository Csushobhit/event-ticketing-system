package com.event.ticketing.eventticketingsystem.controller;

import com.event.ticketing.eventticketingsystem.dto.CreateEventRequest;
import com.event.ticketing.eventticketingsystem.dto.UpdateEventRequest;
import com.event.ticketing.eventticketingsystem.model.Event;
import com.event.ticketing.eventticketingsystem.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Event> createEvent(
            @RequestBody CreateEventRequest request
    ) {

        Event createdEvent =
                eventService.createEvent(request);

        URI location =
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(createdEvent.getId())
                        .toUri();

        return ResponseEntity
                .created(location)
                .body(createdEvent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(
            @PathVariable Long id
    ) {

        Event event =
                eventService.getEventById(id);

        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @RequestBody UpdateEventRequest request
    ) {

        Event updatedEvent =
                eventService.updateEvent(
                        id,
                        request
                );

        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id
    ) {

        eventService.deleteEvent(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping
    public ResponseEntity<Page<Event>> getAllEvents(

            Pageable pageable,

            @RequestParam(required = false)
            String title,

            @RequestParam(required = false)
            String location,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date
    ) {

        Page<Event> eventsPage =
                eventService.getAllEvents(
                        pageable,
                        title,
                        location,
                        date
                );

        return ResponseEntity.ok(eventsPage);
    }
}