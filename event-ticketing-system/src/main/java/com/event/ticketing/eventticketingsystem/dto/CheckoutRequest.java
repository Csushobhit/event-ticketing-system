package com.event.ticketing.eventticketingsystem.dto;

import lombok.Data;

@Data
public class CheckoutRequest {

    private Long eventId;

    private Integer ticketQuantity;
}