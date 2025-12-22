package com.flightmanagement.repository;

import com.flightmanagement.entity.Airport;
import com.flightmanagement.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {
    
    @Query("SELECT f FROM Flight f WHERE f.deletedAt IS NULL")
    List<Flight> findAllActive();

    Page<Flight> findByDeletedAtIsNull(Pageable pageable);
    
    @Query("SELECT f FROM Flight f WHERE f.flightId = ?1 AND f.deletedAt IS NULL")
    Optional<Flight> findActiveById(Integer id);
    
    @Query("SELECT f FROM Flight f WHERE f.flightCode = ?1 AND f.deletedAt IS NULL")
    Optional<Flight> findByFlightCode(String flightCode);
    
    @Query("SELECT f FROM Flight f WHERE f.departureAirport.airportId = :departureId AND f.arrivalAirport.airportId = :arrivalId AND DATE(f.departureTime) = DATE(:departureDate) AND f.deletedAt IS NULL")
    List<Flight> findFlights(@Param("departureId") Integer departureAirportId, 
                           @Param("arrivalId") Integer arrivalAirportId, 
                           @Param("departureDate") LocalDateTime departureDate);
    
    @Query("SELECT f FROM Flight f WHERE f.departureTime >= :startDate AND f.departureTime <= :endDate AND f.deletedAt IS NULL")
    List<Flight> findByDepartureDateRange(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT f FROM Flight f WHERE f.plane.planeId = :planeId AND f.deletedAt IS NULL")
    List<Flight> findByPlaneId(@Param("planeId") Integer planeId);
    
    @Query("SELECT COUNT(f) FROM Flight f WHERE (f.departureAirport.airportId = :airportId OR f.arrivalAirport.airportId = :airportId) AND f.deletedAt IS NULL")
    Long countFlightsByAirport(@Param("airportId") Integer airportId);
    
    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :startTime AND :endTime AND f.deletedAt IS NULL ORDER BY f.departureTime ASC")
    List<Flight> findFlightsInTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT DISTINCT f.departureAirport FROM Flight f WHERE f.deletedAt IS NULL")
    List<Airport> findAllDepartureAirports();
    
    @Query("SELECT DISTINCT f.arrivalAirport FROM Flight f WHERE f.deletedAt IS NULL")
    List<Airport> findAllArrivalAirports();
    
    @Query("SELECT f FROM Flight f WHERE DATE(f.departureTime) = DATE(:departureDate) AND f.deletedAt IS NULL")
    List<Flight> findByDepartureDate(@Param("departureDate") LocalDate departureDate);
    
    @Query("SELECT f FROM Flight f WHERE f.departureAirport.airportId = :departureAirportId AND f.arrivalAirport.airportId = :arrivalAirportId AND f.deletedAt IS NULL")
    List<Flight> findByDepartureAirportIdAndArrivalAirportId(
        @Param("departureAirportId") Integer departureAirportId, 
        @Param("arrivalAirportId") Integer arrivalAirportId
    );

    Boolean existsByFlightCode(String flightCode);
    
    @Query("SELECT f FROM Flight f WHERE f.departureAirport.airportId = ?1 AND f.arrivalAirport.airportId = ?2 AND f.departureTime BETWEEN ?3 AND ?4 AND f.deletedAt IS NULL")
    List<Flight> findFlightsByRouteAndDate(Integer departureAirportId, Integer arrivalAirportId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN ?1 AND ?2 AND f.deletedAt IS NULL")
    List<Flight> findFlightsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT CASE WHEN COUNT(ftc) > 0 AND ftc.remainingTicketQuantity >= ?3 THEN true ELSE false END " +
           "FROM FlightTicketClass ftc WHERE ftc.flightId = ?1 AND ftc.ticketClassId = ?2")
    boolean checkSeatAvailability(Integer flightId, Integer ticketClassId, Integer passengerCount);

    @Query("SELECT DISTINCT f FROM Flight f " +
           "WHERE f.departureAirport.airportId = :departureAirportId " +
           "AND f.arrivalAirport.airportId = :arrivalAirportId " +
           "AND DATE(f.departureTime) = DATE(:departureDate) " +
           "AND f.deletedAt IS NULL " +
           "ORDER BY f.departureTime")
    List<Flight> findFlightsByRoute(@Param("departureAirportId") Integer departureAirportId,
                                   @Param("arrivalAirportId") Integer arrivalAirportId,
                                   @Param("departureDate") LocalDateTime departureDate);

    @Query("SELECT DISTINCT f FROM Flight f " +
           "JOIN FlightTicketClass ftc ON f.flightId = ftc.flightId " +
           "WHERE f.departureAirport.airportId = :departureAirportId " +
           "AND f.arrivalAirport.airportId = :arrivalAirportId " +
           "AND DATE(f.departureTime) = DATE(:departureDate) " +
           "AND ftc.ticketClassId = :ticketClassId " +
           "AND ftc.remainingTicketQuantity >= :passengerCount " +
           "AND f.deletedAt IS NULL " +
           "AND ftc.deletedAt IS NULL " +
           "ORDER BY f.departureTime")
    List<Flight> findFlightsWithTicketClass(@Param("departureAirportId") Integer departureAirportId,
                                           @Param("arrivalAirportId") Integer arrivalAirportId,
                                           @Param("departureDate") LocalDateTime departureDate,
                                           @Param("ticketClassId") Integer ticketClassId,
                                           @Param("passengerCount") Integer passengerCount);
}
