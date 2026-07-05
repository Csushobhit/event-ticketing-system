package com.event.ticketing.eventticketingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailDto {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime date;
    private String location;
    private BigDecimal ticketPrice;
    private Integer totalTicketsAvailable;
    private OrganizerDto organizer;
}