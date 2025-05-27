package com.flightmanagement.repository;

import com.flightmanagement.entity.FlightTicketClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightTicketClassRepository extends JpaRepository<FlightTicketClass, Integer> {
    
    @Query("SELECT ftc FROM FlightTicketClass ftc WHERE ftc.deletedAt IS NULL")
    List<FlightTicketClass> findAllActive();
    
    @Query("SELECT ftc FROM FlightTicketClass ftc WHERE ftc.flightId = :flightId AND ftc.ticketClassId = :ticketClassId AND ftc.deletedAt IS NULL")
    Optional<FlightTicketClass> findByFlightIdAndTicketClassId(@Param("flightId") Integer flightId, @Param("ticketClassId") Integer ticketClassId);
    
    @Query("SELECT ftc FROM FlightTicketClass ftc WHERE ftc.flightId = :flightId AND ftc.deletedAt IS NULL")
    List<FlightTicketClass> findByFlightId(@Param("flightId") Integer flightId);
    
    @Query("SELECT ftc FROM FlightTicketClass ftc WHERE ftc.ticketClassId = :ticketClassId AND ftc.deletedAt IS NULL")
    List<FlightTicketClass> findByTicketClassId(@Param("ticketClassId") Integer ticketClassId);
    
    @Query("SELECT ftc FROM FlightTicketClass ftc WHERE ftc.remainingTicketQuantity > 0 AND ftc.deletedAt IS NULL")
    List<FlightTicketClass> findAvailable();
    
    @Query("SELECT ftc FROM FlightTicketClass ftc JOIN ftc.flight f WHERE f.departureTime > CURRENT_TIMESTAMP AND ftc.remainingTicketQuantity > 0 AND ftc.deletedAt IS NULL")
    List<FlightTicketClass> findAvailableForFutureFlights();
}
