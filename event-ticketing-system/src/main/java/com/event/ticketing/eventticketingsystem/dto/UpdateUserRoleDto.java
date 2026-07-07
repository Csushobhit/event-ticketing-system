package com.event.ticketing.eventticketingsystem.dto;

import com.event.ticketing.eventticketingsystem.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserRoleDto {

    @NotNull
    private Role newRole;
}