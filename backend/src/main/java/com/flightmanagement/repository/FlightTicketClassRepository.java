package com.flightmanagement.repository;

import com.flightmanagement.entity.FlightTicketClass;
import com.flightmanagement.entity.composite.FlightTicketClassId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightTicketClassRepository extends JpaRepository<FlightTicketClass, FlightTicketClassId> {
    
    @Query("SELECT ftc FROM FlightTicketClass ftc WHERE ftc.deletedAt IS NULL")
    List<FlightTicketClass> findAllActive();
    
    @Query("SELECT ftc FROM FlightTicketClass ftc WHERE ftc.flightId = ?1 AND ftc.deletedAt IS NULL")
    List<FlightTicketClass> findByFlightId(Integer flightId);
    
    @Query("SELECT ftc FROM FlightTicketClass ftc WHERE ftc.ticketClassId = ?1 AND ftc.deletedAt IS NULL")
    List<FlightTicketClass> findByTicketClassId(Integer ticketClassId);
    
    @Query("SELECT ftc FROM FlightTicketClass ftc WHERE ftc.flightId = ?1 AND ftc.ticketClassId = ?2 AND ftc.deletedAt IS NULL")
    Optional<FlightTicketClass> findByFlightIdAndTicketClassId(Integer flightId, Integer ticketClassId);
    
    @Query("SELECT ftc FROM FlightTicketClass ftc WHERE ftc.remainingTicketQuantity > 0 AND ftc.deletedAt IS NULL")
    List<FlightTicketClass> findAvailableTickets();
}
