package com.example.sprint01.repository;

import com.example.sprint01.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE f.deletedAt IS NULL")
    List<Flight> findAllActive();

    @Query("SELECT f FROM Flight f WHERE f.id = ?1 AND f.deletedAt IS NULL")
    Optional<Flight> findActiveById(Long id);
}