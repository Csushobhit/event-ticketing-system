package com.event.ticketing.eventticketingsystem.repository;

import com.event.ticketing.eventticketingsystem.model.Event;
import com.event.ticketing.eventticketingsystem.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository
        extends JpaRepository<Event, Long>,
        JpaSpecificationExecutor<Event> {

    List<Event> findByOrganizer(User organizer);

}