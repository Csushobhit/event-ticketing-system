package com.event.ticketing.eventticketingsystem.repository;

import com.event.ticketing.eventticketingsystem.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}