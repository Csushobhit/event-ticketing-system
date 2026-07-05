package com.event.ticketing.eventticketingsystem.controller;

import com.event.ticketing.eventticketingsystem.model.Event;
import com.event.ticketing.eventticketingsystem.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/organizer")
@RequiredArgsConstructor
public class OrganizerController {

    private final EventService eventService;

    @GetMapping("/events")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<Event>> getOrganizerEvents() {

        List<Event> events =
                eventService.getEventsByOrganizer();

        return ResponseEntity.ok(events);
    }
}