package com.event.ticketing.eventticketingsystem.controller;

import com.event.ticketing.eventticketingsystem.dto.AttendeeDto;
import com.event.ticketing.eventticketingsystem.dto.OrganizerDashboardDto;
import com.event.ticketing.eventticketingsystem.model.Event;
import com.event.ticketing.eventticketingsystem.service.EventService;
import com.event.ticketing.eventticketingsystem.service.OrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizer")
@RequiredArgsConstructor
public class OrganizerController {

    private final EventService eventService;

    private final OrganizerService organizerService;

    @GetMapping("/events")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<Event>> getOrganizerEvents() {

        List<Event> events =
                eventService.getEventsByOrganizer();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/dashboard/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<OrganizerDashboardDto> getDashboardData(
            @PathVariable Long eventId
    ) {

        return ResponseEntity.ok(
                organizerService.getDashboardData(
                        eventId
                )
        );
    }

    @GetMapping("/events/{eventId}/attendees")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<AttendeeDto>> getEventAttendees(
            @PathVariable Long eventId
    ) {

        return ResponseEntity.ok(
                organizerService.getEventAttendees(
                        eventId
                )
        );
    }
}