package com.event.ticketing.eventticketingsystem.repository;

import com.event.ticketing.eventticketingsystem.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByPaymentIntentId(
            String paymentIntentId
    );
}