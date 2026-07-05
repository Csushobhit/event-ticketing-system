package com.event.ticketing.eventticketingsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Public Endpoint");
    }

    @GetMapping("/user-only")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> userEndpoint() {
        return ResponseEntity.ok("Authenticated User");
    }

    @GetMapping("/organizer-only")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<String> organizerEndpoint() {
        return ResponseEntity.ok("Organizer Only");
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("Admin Only");
    }
}