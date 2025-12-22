package com.flightmanagement.repository;

import com.flightmanagement.entity.FlightDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightDetailRepository extends JpaRepository<FlightDetail, Integer> {
    
    @Query("SELECT fd FROM FlightDetail fd WHERE fd.deletedAt IS NULL")
    List<FlightDetail> findAllActive();

    Page<FlightDetail> findByDeletedAtIsNull(Pageable pageable);
    
    @Query("SELECT fd FROM FlightDetail fd WHERE fd.flightId = :flightId AND fd.mediumAirportId = :mediumAirportId AND fd.deletedAt IS NULL")
    Optional<FlightDetail> findByFlightIdAndMediumAirportId(@Param("flightId") Integer flightId, @Param("mediumAirportId") Integer mediumAirportId);
    
    @Query("SELECT fd FROM FlightDetail fd WHERE fd.flightId = :flightId AND fd.deletedAt IS NULL")
    List<FlightDetail> findByFlightId(@Param("flightId") Integer flightId);
    
    @Query("SELECT fd FROM FlightDetail fd WHERE fd.mediumAirportId = :mediumAirportId AND fd.deletedAt IS NULL")
    List<FlightDetail> findByMediumAirportId(@Param("mediumAirportId") Integer mediumAirportId);
}
