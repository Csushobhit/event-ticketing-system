package com.event.ticketing.eventticketingsystem.controller;

import com.event.ticketing.eventticketingsystem.dto.AuthenticationRequest;
import com.event.ticketing.eventticketingsystem.dto.AuthenticationResponse;
import com.event.ticketing.eventticketingsystem.dto.RegisterRequest;
import com.event.ticketing.eventticketingsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request
    ) {

        return ResponseEntity.ok(
                authService.authenticate(request)
        );
    }
}