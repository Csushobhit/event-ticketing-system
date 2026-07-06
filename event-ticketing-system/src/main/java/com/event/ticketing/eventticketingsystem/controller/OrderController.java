package com.event.ticketing.eventticketingsystem.controller;

import com.event.ticketing.eventticketingsystem.dto.CheckoutRequest;
import com.event.ticketing.eventticketingsystem.dto.CheckoutResponse;
import com.event.ticketing.eventticketingsystem.service.OrderService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CheckoutResponse> checkout(
            @RequestBody CheckoutRequest request
    ) throws StripeException {

        String clientSecret =
                orderService.createPaymentIntent(
                        request
                );

        return ResponseEntity.ok(
                new CheckoutResponse(
                        clientSecret
                )
        );
    }
}