package com.example.sprint01.repository;

import com.example.sprint01.entity.FlightDetails;
import com.example.sprint01.entity.composite_key.FlightDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FlightDetailsRepository extends JpaRepository<FlightDetails, FlightDetailsId> {
    @Query("SELECT fd FROM FlightDetails fd WHERE fd.deletedAt IS NULL")
    List<FlightDetails> findAllActive();

    @Query("SELECT fd FROM FlightDetails fd WHERE fd.id.flightId = ?1 AND fd.id.mediumAirportId = ?2 AND fd.deletedAt IS NULL")
    Optional<FlightDetails> findActiveById(Long flightId, Long mediumAirportId);
}