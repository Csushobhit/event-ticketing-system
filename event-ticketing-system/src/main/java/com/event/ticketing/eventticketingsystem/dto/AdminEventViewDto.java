package com.event.ticketing.eventticketingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventViewDto {

    private Long id;

    private String title;

    private LocalDateTime date;

    private String location;

    private BigDecimal ticketPrice;

    private Integer ticketsSold;

    private Integer totalTicketsAvailable;

    private Long organizerId;

    private String organizerName;
}