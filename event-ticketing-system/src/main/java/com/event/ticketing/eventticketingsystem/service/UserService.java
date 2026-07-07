package com.event.ticketing.eventticketingsystem.service;

import com.event.ticketing.eventticketingsystem.dto.EventSummaryDto;
import com.event.ticketing.eventticketingsystem.dto.TicketDto;
import com.event.ticketing.eventticketingsystem.model.Ticket;
import com.event.ticketing.eventticketingsystem.model.User;
import com.event.ticketing.eventticketingsystem.repository.TicketRepository;
import com.event.ticketing.eventticketingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public List<TicketDto> getMyTickets() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email
                        )
                );

        List<Ticket> tickets = ticketRepository.findByOrder_User(user);

        return tickets.stream()
                .map(this::mapToTicketDto)
                .toList();
    }

    private TicketDto mapToTicketDto(Ticket ticket) {

        EventSummaryDto eventDto = EventSummaryDto.builder()
                .id(ticket.getEvent().getId())
                .title(ticket.getEvent().getTitle())
                .date(ticket.getEvent().getDate())
                .location(ticket.getEvent().getLocation())
                .build();

        return TicketDto.builder()
                .uniqueCode(ticket.getUniqueCode())
                .event(eventDto)
                .build();
    }
}