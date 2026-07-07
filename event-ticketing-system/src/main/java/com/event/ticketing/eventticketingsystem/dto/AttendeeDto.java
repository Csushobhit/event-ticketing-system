package com.event.ticketing.eventticketingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeDto {

    private UUID uniqueCode;

    private String userName;

    private String userEmail;
}