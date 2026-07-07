package com.event.ticketing.eventticketingsystem.service;

import com.event.ticketing.eventticketingsystem.dto.UserDto;
import com.event.ticketing.eventticketingsystem.model.User;
import com.event.ticketing.eventticketingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.event.ticketing.eventticketingsystem.dto.AdminEventViewDto;
import com.event.ticketing.eventticketingsystem.model.Event;
import com.event.ticketing.eventticketingsystem.repository.EventRepository;


import com.event.ticketing.eventticketingsystem.model.Role;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.event.ticketing.eventticketingsystem.dto.PlatformAnalyticsDto;
import java.math.BigDecimal;

import java.util.Set;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {

        List<User> users =
                userRepository.findAll();

        return users.stream()
                .map(this::mapToUserDto)
                .toList();
    }
    @Transactional(readOnly = true)
    public PlatformAnalyticsDto getPlatformAnalytics() {

        BigDecimal totalRevenue =
                eventRepository.calculateTotalRevenue();

        Long totalTicketsSold =
                eventRepository.calculateTotalTicketsSold();

        Long totalUsers =
                userRepository.count();

        Long totalEvents =
                eventRepository.count();

        return PlatformAnalyticsDto.builder()
                .totalRevenue(totalRevenue)
                .totalTicketsSold(totalTicketsSold)
                .totalUsers(totalUsers)
                .totalEvents(totalEvents)
                .build();
    }

    private UserDto mapToUserDto(
            User user
    ) {

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
    @Transactional(readOnly = true)
    public List<AdminEventViewDto> getAllEvents() {

        List<Event> events =
                eventRepository.findAll();

        return events.stream()
                .map(this::mapToAdminEventViewDto)
                .toList();
    }
    
    private AdminEventViewDto mapToAdminEventViewDto(
            Event event
    ) {

        return AdminEventViewDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .date(event.getDate())
                .location(event.getLocation())
                .ticketPrice(event.getTicketPrice())
                .ticketsSold(event.getTicketsSold())
                .totalTicketsAvailable(
                        event.getTotalTicketsAvailable()
                )
                .organizerId(
                        event.getOrganizer().getId()
                )
                .organizerName(
                        event.getOrganizer().getName()
                )
                .build();
    }
    
    @Transactional
    public UserDto updateUserRole(
            Long userId,
            Role newRole
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "User not found with ID: "
                                                + userId
                                )
                        );

        String currentAdminEmail =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        if (user.getEmail().equals(currentAdminEmail)) {

            throw new IllegalStateException(
                    "Admins cannot change their own role."
            );
        }

        user.setRoles(
                Set.of(newRole)
        );

        User updatedUser =
                userRepository.save(user);

        return mapToUserDto(updatedUser);
    }
}