package com.event.ticketing.eventticketingsystem.repository;

import com.event.ticketing.eventticketingsystem.model.Ticket;
import com.event.ticketing.eventticketingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    List<Ticket> findByOrder_User(User user);

    List<Ticket> findByEvent_Id(Long eventId);
}