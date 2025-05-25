package com.flightmanagement.repository;

import com.flightmanagement.entity.Airport;
import com.flightmanagement.entity.Flight;
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
}
