package com.example.sprint01.repository;

import com.example.sprint01.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    // Custom query methods can be defined here if needed
}
