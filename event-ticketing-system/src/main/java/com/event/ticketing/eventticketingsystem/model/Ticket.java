package com.event.ticketing.eventticketingsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ticket_sequence"
    )
    @SequenceGenerator(
            name = "ticket_sequence",
            sequenceName = "ticket_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "unique_code", nullable = false, unique = true, updatable = false)
    private UUID uniqueCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @PrePersist
    public void prePersist() {
        if (this.uniqueCode == null) {
            this.uniqueCode = UUID.randomUUID();
        }
    }
}