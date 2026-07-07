package com.event.ticketing.eventticketingsystem.controller;

import com.event.ticketing.eventticketingsystem.dto.TicketDto;
import com.event.ticketing.eventticketingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me/tickets")
    public ResponseEntity<List<TicketDto>> getMyTickets() {

        return ResponseEntity.ok(
                userService.getMyTickets()
        );
    }
}