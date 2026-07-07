package com.event.ticketing.eventticketingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EventTicketingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventTicketingSystemApplication.class, args);
	}

}
