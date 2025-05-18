package com.example.sprint01.repository;

import com.example.sprint01.entity.FlightDetails;
import com.example.sprint01.entity.composite_key.FlightDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightDetailsRepository extends JpaRepository<FlightDetails, FlightDetailsId> {
}
