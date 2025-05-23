package com.example.sprint01.repository;

import com.example.sprint01.entity.FlightSeatClass;
import com.example.sprint01.entity.composite_key.FlightSeatClassId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FlightSeatClassRepository extends JpaRepository<FlightSeatClass, FlightSeatClassId> {
    @Query("SELECT fd FROM FlightSeatClass fd WHERE fd.deletedAt IS NULL")
    List<FlightSeatClass> findAllActive();

    @Query("SELECT fd FROM FlightSeatClass fd WHERE fd.id.flightId = ?1 AND fd.id.seatClassId = ?2 AND fd.deletedAt IS NULL")
    Optional<FlightSeatClass> findActiveById(Long flightId, Long seatClassId);

    @Query("SELECT fd FROM FlightSeatClass fd WHERE fd.flight.id = ?1 AND fd.deletedAt IS NULL")
    List<FlightSeatClass> findAllActiveByFlightId(Long flightId);
}