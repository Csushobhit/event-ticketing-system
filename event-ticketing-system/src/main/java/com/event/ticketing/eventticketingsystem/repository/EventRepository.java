package com.event.ticketing.eventticketingsystem.repository;

import com.event.ticketing.eventticketingsystem.model.Event;
import com.event.ticketing.eventticketingsystem.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository
        extends JpaRepository<Event, Long>,
        JpaSpecificationExecutor<Event> {

    List<Event> findByOrganizer(User organizer);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Event> findAndLockById(Long id);
    
    @Query("""
    	       SELECT COALESCE(
    	           SUM(e.ticketPrice * e.ticketsSold),
    	           0
    	       )
    	       FROM Event e
    	       """)
    	BigDecimal calculateTotalRevenue();

    	@Query("""
    	       SELECT COALESCE(
    	           SUM(e.ticketsSold),
    	           0
    	       )
    	       FROM Event e
    	       """)
    	Long calculateTotalTicketsSold();
    	
    	

}