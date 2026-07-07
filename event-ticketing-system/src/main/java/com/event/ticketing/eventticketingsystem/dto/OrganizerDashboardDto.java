package com.event.ticketing.eventticketingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerDashboardDto {

    private Long eventId;

    private String eventTitle;

    private BigDecimal totalRevenue;

    private Integer ticketsSold;

    private Integer ticketsAvailable;

    private Integer totalTickets;
}