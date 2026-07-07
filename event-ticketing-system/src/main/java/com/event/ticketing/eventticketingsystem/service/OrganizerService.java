package com.event.ticketing.eventticketingsystem.service;

import com.event.ticketing.eventticketingsystem.dto.AttendeeDto;
import com.event.ticketing.eventticketingsystem.dto.OrganizerDashboardDto;
import com.event.ticketing.eventticketingsystem.model.Event;
import com.event.ticketing.eventticketingsystem.model.Ticket;
import com.event.ticketing.eventticketingsystem.model.User;
import com.event.ticketing.eventticketingsystem.repository.EventRepository;
import com.event.ticketing.eventticketingsystem.repository.TicketRepository;
import com.event.ticketing.eventticketingsystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public OrganizerDashboardDto getDashboardData(
            Long eventId
    ) {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User organizer =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new UsernameNotFoundException(
                                        "Organizer not found"
                                )
                        );

        Event event =
                eventRepository
                        .findById(eventId)
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "Event not found with ID: "
                                                + eventId
                                )
                        );

        if (!event.getOrganizer()
                .getId()
                .equals(organizer.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to access this event."
            );
        }

        BigDecimal totalRevenue =
                event.getTicketPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        event.getTicketsSold()
                                )
                        );

        return OrganizerDashboardDto.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .totalRevenue(totalRevenue)
                .ticketsSold(event.getTicketsSold())
                .ticketsAvailable(
                        event.getTotalTicketsAvailable()
                                - event.getTicketsSold()
                )
                .totalTickets(
                        event.getTotalTicketsAvailable()
                )
                .build();
    }

    @Transactional(readOnly = true)
    public List<AttendeeDto> getEventAttendees(
            Long eventId
    ) {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User organizer =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new UsernameNotFoundException(
                                        "Organizer not found"
                                )
                        );

        Event event =
                eventRepository
                        .findById(eventId)
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "Event not found with ID: "
                                                + eventId
                                )
                        );

        if (!event.getOrganizer()
                .getId()
                .equals(organizer.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to access this event."
            );
        }

        List<Ticket> tickets =
                ticketRepository.findByEvent_Id(
                        eventId
                );

        return tickets.stream()
                .map(this::mapToAttendeeDto)
                .toList();
    }

    private AttendeeDto mapToAttendeeDto(
            Ticket ticket
    ) {

        return AttendeeDto.builder()
                .uniqueCode(
                        ticket.getUniqueCode()
                )
                .userName(
                        ticket.getOrder()
                                .getUser()
                                .getName()
                )
                .userEmail(
                        ticket.getOrder()
                                .getUser()
                                .getEmail()
                )
                .build();
    }
}