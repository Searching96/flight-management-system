package com.example.sprint01.repository;

import com.example.sprint01.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    // Custom query methods can be defined here if needed

}
