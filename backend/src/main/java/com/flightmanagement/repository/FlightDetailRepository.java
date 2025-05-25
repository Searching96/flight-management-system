package com.flightmanagement.repository;

import com.flightmanagement.entity.FlightDetail;
import com.flightmanagement.entity.composite.FlightDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightDetailRepository extends JpaRepository<FlightDetail, FlightDetailId> {
    
    @Query("SELECT fd FROM FlightDetail fd WHERE fd.deletedAt IS NULL")
    List<FlightDetail> findAllActive();
    
    @Query("SELECT fd FROM FlightDetail fd WHERE fd.flightId = ?1 AND fd.mediumAirportId = ?2 AND fd.deletedAt IS NULL")
    Optional<FlightDetail> findActiveById(Integer flightId, Integer mediumAirportId);
    
    @Query("SELECT fd FROM FlightDetail fd WHERE fd.flightId = ?1 AND fd.deletedAt IS NULL")
    List<FlightDetail> findByFlightId(Integer flightId);
    
    @Query("SELECT fd FROM FlightDetail fd WHERE fd.mediumAirportId = ?1 AND fd.deletedAt IS NULL")
    List<FlightDetail> findByMediumAirportId(Integer mediumAirportId);
}
