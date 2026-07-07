package com.event.ticketing.eventticketingsystem.controller;

import com.event.ticketing.eventticketingsystem.dto.UserDto;
import com.event.ticketing.eventticketingsystem.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.event.ticketing.eventticketingsystem.dto.AdminEventViewDto;

import com.event.ticketing.eventticketingsystem.dto.UpdateUserRoleDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.event.ticketing.eventticketingsystem.dto.PlatformAnalyticsDto;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {

        return ResponseEntity.ok(
                adminService.getAllUsers()
        );
    }
    @GetMapping("/events")
    public ResponseEntity<List<AdminEventViewDto>> getAllEvents() {

        return ResponseEntity.ok(
                adminService.getAllEvents()
        );
    }
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserDto> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleDto roleDto
    ) {

        return ResponseEntity.ok(
                adminService.updateUserRole(
                        userId,
                        roleDto.getNewRole()
                )
        );
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<PlatformAnalyticsDto>
    getPlatformAnalytics() {

        return ResponseEntity.ok(
                adminService.getPlatformAnalytics()
        );
    }
    
}