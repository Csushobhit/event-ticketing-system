package com.event.ticketing.eventticketingsystem.service;

import com.event.ticketing.eventticketingsystem.dto.CheckoutRequest;
import com.event.ticketing.eventticketingsystem.model.Event;
import com.event.ticketing.eventticketingsystem.model.Order;
import com.event.ticketing.eventticketingsystem.model.Ticket;
import com.event.ticketing.eventticketingsystem.model.User;
import com.event.ticketing.eventticketingsystem.repository.EventRepository;
import com.event.ticketing.eventticketingsystem.repository.OrderRepository;
import com.event.ticketing.eventticketingsystem.repository.TicketRepository;
import com.event.ticketing.eventticketingsystem.repository.UserRepository;
import com.event.ticketing.eventticketingsystem.exception.InsufficientTicketsException;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final PdfGenerationService pdfGenerationService;
    private final EmailService emailService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    
    @Transactional
    public String createPaymentIntent(
            CheckoutRequest request
    ) throws StripeException {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Current user not found"
                                )
                        );

        Event event =
                eventRepository.findAndLockById(
                        request.getEventId()
                )
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Event not found with ID: "
                                        + request.getEventId()
                        )
                );

        int ticketsAvailable =
                event.getTotalTicketsAvailable()
                        - event.getTicketsSold();

        if (ticketsAvailable < request.getTicketQuantity()) {

            throw new InsufficientTicketsException(
                    "Not enough tickets available for event: "
                            + event.getTitle()
                            + ". Requested: "
                            + request.getTicketQuantity()
                            + ", Available: "
                            + ticketsAvailable
            );
        }

        BigDecimal totalAmount =
                event.getTicketPrice()
                        .multiply(
                                new BigDecimal(
                                        request.getTicketQuantity()
                                )
                        );

        long amountInCents =
                totalAmount
                        .multiply(
                                new BigDecimal(100)
                        )
                        .longValue();

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams
                        .builder()
                        .setAmount(amountInCents)
                        .setCurrency("usd")

                        .putMetadata(
                                "userId",
                                user.getId().toString()
                        )

                        .putMetadata(
                                "eventId",
                                event.getId().toString()
                        )

                        .putMetadata(
                                "ticketQuantity",
                                request.getTicketQuantity().toString()
                        )

                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams
                                        .AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )

                        .build();

        PaymentIntent paymentIntent =
                PaymentIntent.create(params);

        return paymentIntent.getClientSecret();
    }
    public void handleStripeEvent(
            String payload,
            String sigHeader
    ) {

        com.stripe.model.Event stripeEvent;

        try {

            stripeEvent =
                    Webhook.constructEvent(
                            payload,
                            sigHeader,
                            webhookSecret
                    );

        } catch (SignatureVerificationException e) {

            log.error(
                    "Stripe webhook signature verification failed!",
                    e
            );

            return;

        } catch (Exception e) {

            log.error(
                    "Error parsing webhook JSON: {}",
                    e.getMessage()
            );

            return;
        }

        log.info(
                "Stripe event signature verified. Type: {}, ID: {}",
                stripeEvent.getType(),
                stripeEvent.getId()
        );

        Optional<StripeObject> stripeObject =
                stripeEvent
                        .getDataObjectDeserializer()
                        .getObject();

        if (stripeObject.isEmpty()) {

            log.error(
                    "Stripe event data object is empty. Event ID: {}",
                    stripeEvent.getId()
            );

            return;
        }

        switch (stripeEvent.getType()) {

            case "payment_intent.succeeded":

                PaymentIntent paymentIntent =
                        (PaymentIntent) stripeObject.get();

                log.info(
                        "Payment succeeded for PaymentIntent: {}",
                        paymentIntent.getId()
                );

                fulfillOrder(paymentIntent);

                break;

            case "payment_intent.payment_failed":

                PaymentIntent failedPaymentIntent =
                        (PaymentIntent) stripeObject.get();

                log.warn(
                        "Payment failed for PaymentIntent: {}. Reason: {}",
                        failedPaymentIntent.getId(),
                        failedPaymentIntent.getLastPaymentError() != null
                                ? failedPaymentIntent.getLastPaymentError().getMessage()
                                : "No reason provided"
                );

                break;

            default:

                log.warn(
                        "Unhandled event type: {}",
                        stripeEvent.getType()
                );

                break;
        }
    }


    @Transactional
    public void fulfillOrder(
            PaymentIntent paymentIntent
    ) {

        if (
                orderRepository.existsByPaymentIntentId(
                        paymentIntent.getId()
                )
        ) {

            log.warn(
                    "Order already processed for PaymentIntent: {}",
                    paymentIntent.getId()
            );

            return;
        }

        Long userId =
                Long.parseLong(
                        paymentIntent
                                .getMetadata()
                                .get("userId")
                );

        Long eventId =
                Long.parseLong(
                        paymentIntent
                                .getMetadata()
                                .get("eventId")
                );

        int ticketQuantity =
                Integer.parseInt(
                        paymentIntent
                                .getMetadata()
                                .get("ticketQuantity")
                );

        Event event =
                eventRepository
                        .findAndLockById(eventId)
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "Event not found"
                                )
                        );

        int ticketsAvailable =
                event.getTotalTicketsAvailable()
                        - event.getTicketsSold();

        if (ticketsAvailable < ticketQuantity) {

            log.error(
                    "Oversell attempt detected for Event {}",
                    event.getId()
            );

            throw new InsufficientTicketsException(
                    "Oversell detected during order fulfillment."
            );
        }

        event.setTicketsSold(
                event.getTicketsSold() + ticketQuantity
        );

        eventRepository.save(event);

        User user =
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "User not found"
                                )
                        );

        Order order =
                Order.builder()
                        .orderDate(LocalDateTime.now())
                        .totalAmount(
                                BigDecimal.valueOf(
                                        paymentIntent.getAmount()
                                ).divide(
                                        BigDecimal.valueOf(100)
                                )
                        )
                        .paymentIntentId(
                                paymentIntent.getId()
                        )
                        .status("COMPLETED")
                        .user(user)
                        .build();

        Order savedOrder =
                orderRepository.save(order);

        Set<Ticket> tickets =
                new HashSet<>();

        for (
                int i = 0;
                i < ticketQuantity;
                i++
        ) {

            Ticket ticket =
                    Ticket.builder()
                            .event(event)
                            .order(savedOrder)
                            .build();

            tickets.add(ticket);
        }

        ticketRepository.saveAll(tickets);

        log.info(
                "Successfully fulfilled order {} with {} tickets. Event {} has {} tickets sold.",
                savedOrder.getId(),
                ticketQuantity,
                event.getId(),
                event.getTicketsSold()
        );
        try {

            Ticket firstTicket =
                    tickets.iterator().next();

            byte[] pdfBytes =
                    pdfGenerationService.generateTicketPdf(
                            firstTicket
                    );

            String subject =
                    "Your Ticket for: "
                            + event.getTitle();

            String body =
                    "<h2>Thank you for your purchase!</h2>"
                    + "<p>Hello "
                    + user.getName()
                    + ",</p>"
                    + "<p>Your ticket for <strong>"
                    + event.getTitle()
                    + "</strong> is attached.</p>"
                    + "<p>Order ID: "
                    + savedOrder.getId()
                    + "</p>";

            String attachmentName =
                    "ticket-"
                    + firstTicket.getUniqueCode()
                    + ".pdf";

            emailService.sendEmailWithAttachment(
                    user.getEmail(),
                    subject,
                    body,
                    pdfBytes,
                    attachmentName
            );

            log.info(
                    "Ticket email dispatched for Order {}",
                    savedOrder.getId()
            );

        } catch (Exception e) {

            log.error(
                    "Failed to generate/send ticket email for Order {}",
                    savedOrder.getId(),
                    e
            );

        }
    }
}