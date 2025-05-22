package com.example.sprint01.repository;

import com.example.sprint01.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    @Query("SELECT a FROM Airport a WHERE a.deletedAt IS NULL")
    List<Airport> findAllActive();

    @Query("SELECT a FROM Airport a WHERE a.id = ?1 AND a.deletedAt IS NULL")
    Optional<Airport> findActiveById(Long id);
}