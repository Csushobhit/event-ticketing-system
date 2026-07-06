package com.event.ticketing.eventticketingsystem.controller;

import com.event.ticketing.eventticketingsystem.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final OrderService orderService;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(

            @RequestBody String payload,
            @RequestHeader("Stripe-Signature")
            String sigHeader
    ) {

        log.info(
                "Received Stripe webhook..."
        );

        orderService.handleStripeEvent(
                payload,
                sigHeader
        );

        return ResponseEntity.ok(
                "Webhook received and acknowledged."
        );
    }
}