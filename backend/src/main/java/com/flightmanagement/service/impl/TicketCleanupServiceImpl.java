package com.flightmanagement.service.impl;

import com.flightmanagement.dto.ParameterDto;
import com.flightmanagement.entity.Ticket;
import com.flightmanagement.repository.TicketRepository;
import com.flightmanagement.service.FlightTicketClassService;
import com.flightmanagement.service.ParameterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@ConditionalOnProperty(name = "spring.task.scheduling.enabled", havingValue = "true", matchIfMissing = true)
@Transactional
public class TicketCleanupServiceImpl {

   private final TicketRepository ticketRepository;

   private final ParameterService parameterService;

   private final FlightTicketClassService flightTicketClassService;

   public TicketCleanupServiceImpl(TicketRepository ticketRepository, ParameterService parameterService,
                                   FlightTicketClassService flightTicketClassService) {
      this.ticketRepository = ticketRepository;
      this.parameterService = parameterService;
      this.flightTicketClassService = flightTicketClassService;
   }

   // Run every 1 minutes
   @Scheduled(fixedRate = 60000) // 1 minute in milliseconds
   public void cleanupExpiredUnpaidTickets() {
      try {
         // Get the maximum booking hold duration from parameters
         ParameterDto parameters = parameterService.getLatestParameter();
         int maxBookingHoldDuration = parameters.getMaxBookingHoldDuration();

         // Calculate cutoff time (current time + hold duration hours before flight)
         LocalDateTime cutoffTime = LocalDateTime.now().plusHours(maxBookingHoldDuration);

         // Find unpaid tickets for flights departing within the cutoff time
         List<Ticket> expiredTickets = ticketRepository.findExpiredUnpaidTickets(cutoffTime);

         System.out.println("Found " + expiredTickets.size() + " expired unpaid tickets to cleanup");

         for (Ticket ticket : expiredTickets) {
            // Cancel the ticket (soft delete)
            ticket.setTicketStatus((byte) 0); // 0: canceled
            ticket.setDeletedAt(LocalDateTime.now());
            ticketRepository.save(ticket);

            // Return the seat to available inventory
            flightTicketClassService.updateRemainingTickets(
                  ticket.getFlight().getFlightId(),
                  ticket.getTicketClass().getTicketClassId(),
                  -1 // Add back 1 seat (negative quantity to increase remaining) // TODO
            );

            System.out.println("Canceled expired unpaid ticket ID: " + ticket.getTicketId() +
                  " for flight: " + ticket.getFlight().getFlightCode());
         }

      } catch (Exception e) {
         System.err.println("Error during ticket cleanup: " + e.getMessage());
         e.printStackTrace();
      }
   }
}