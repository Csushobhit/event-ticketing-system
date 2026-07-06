package com.event.ticketing.eventticketingsystem.service;


import com.event.ticketing.eventticketingsystem.dto.CreateEventRequest;
import com.event.ticketing.eventticketingsystem.dto.EventDetailDto;
import com.event.ticketing.eventticketingsystem.dto.OrganizerDto;
import com.event.ticketing.eventticketingsystem.dto.UpdateEventRequest;
import com.event.ticketing.eventticketingsystem.model.Event;
import com.event.ticketing.eventticketingsystem.model.User;
import com.event.ticketing.eventticketingsystem.repository.EventRepository;
import com.event.ticketing.eventticketingsystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Event createEvent(CreateEventRequest request) {

        UserDetails userDetails =
                (UserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        String email = userDetails.getUsername();

        User organizer = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "User not found"
                        )
                );

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .date(request.getDate())
                .location(request.getLocation())
                .ticketPrice(request.getTicketPrice())
                .totalTicketsAvailable(
                        request.getTotalTicketsAvailable()
                )
                .organizer(organizer)
                .build();

        return eventRepository.save(event);
    }

    public EventDetailDto getEventById(Long eventId) {

        Event event = getEventEntityById(eventId);

        return mapToEventDetailDto(event);
    }

    private Event getEventEntityById(Long eventId) {

        return eventRepository.findById(eventId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Event not found with ID: "
                                        + eventId
                        )
                );
    }

    public Event updateEvent(
            Long eventId,
            UpdateEventRequest request
    ) {

        Event existingEvent =
                getEventEntityById(eventId);

        String currentUserEmail =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        String ownerEmail =
                existingEvent
                        .getOrganizer()
                        .getEmail();

        if (!currentUserEmail.equals(ownerEmail)) {
            throw new AccessDeniedException(
                    "You are not the owner of this event"
            );
        }

        existingEvent.setTitle(
                request.getTitle()
        );

        existingEvent.setDescription(
                request.getDescription()
        );

        existingEvent.setDate(
                request.getDate()
        );

        existingEvent.setLocation(
                request.getLocation()
        );

        existingEvent.setTicketPrice(
                request.getTicketPrice()
        );

        existingEvent.setTotalTicketsAvailable(
                request.getTotalTicketsAvailable()
        );

        return eventRepository.save(existingEvent);
    }

    public List<Event> getEventsByOrganizer() {

        String currentUsername =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User organizer =
                userRepository
                        .findByEmail(currentUsername)
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Authenticated user not found"
                                )
                        );

        return eventRepository.findByOrganizer(
                organizer
        );
    }

    public void deleteEvent(Long eventId) {

        Event event =
                getEventEntityById(eventId);

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String currentUsername =
                authentication.getName();

        boolean isAdmin =
                authentication
                        .getAuthorities()
                        .contains(
                                new SimpleGrantedAuthority(
                                        "ROLE_ADMIN"
                                )
                        );

        boolean isOwner =
                event.getOrganizer()
                        .getEmail()
                        .equals(currentUsername);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "You are not authorized to delete this event"
            );
        }

        eventRepository.delete(event);
    }

    public Page<EventDetailDto> getAllEvents(
            Pageable pageable,
            String title,
            String location,
            LocalDate date
    ) {

        Specification<Event> spec =
                (root, query, cb) -> cb.conjunction();

        if (title != null && !title.isBlank()) {

            spec = spec.and(
                    (root, query, cb) ->
                            cb.like(
                                    cb.lower(
                                            root.get("title")
                                    ),
                                    "%" + title.toLowerCase() + "%"
                            )
            );
        }

        if (location != null && !location.isBlank()) {

            spec = spec.and(
                    (root, query, cb) ->
                            cb.like(
                                    cb.lower(
                                            root.get("location")
                                    ),
                                    "%" + location.toLowerCase() + "%"
                            )
            );
        }

        if (date != null) {

            spec = spec.and(
                    (root, query, cb) ->
                            cb.greaterThanOrEqualTo(
                                    root.get("date"),
                                    date.atStartOfDay()
                            )
            );
        }

        Page<Event> eventPage =
                eventRepository.findAll(
                        spec,
                        pageable
                );

        return eventPage.map(
                this::mapToEventDetailDto
        );
    }

    private EventDetailDto mapToEventDetailDto(
            Event event
    ) {

        OrganizerDto organizerDto =
                OrganizerDto.builder()
                        .id(event.getOrganizer().getId())
                        .name(event.getOrganizer().getName())
                        .build();

        return EventDetailDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .date(event.getDate())
                .location(event.getLocation())
                .ticketPrice(event.getTicketPrice())
                .totalTicketsAvailable(
                        event.getTotalTicketsAvailable()
                )
                .organizer(organizerDto)
                .build();
    }
}