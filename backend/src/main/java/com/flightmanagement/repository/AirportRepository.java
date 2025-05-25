package com.flightmanagement.repository;

import com.flightmanagement.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Integer> {
    
    @Query("SELECT a FROM Airport a WHERE a.deletedAt IS NULL")
    List<Airport> findAllActive();
    
    @Query("SELECT a FROM Airport a WHERE a.airportId = ?1 AND a.deletedAt IS NULL")
    Optional<Airport> findActiveById(Integer id);
    
    @Query("SELECT a FROM Airport a WHERE a.cityName = ?1 AND a.deletedAt IS NULL")
    List<Airport> findByCityName(String cityName);
    
    @Query("SELECT a FROM Airport a WHERE a.countryName = ?1 AND a.deletedAt IS NULL")
    List<Airport> findByCountryName(String countryName);
    
    @Query("SELECT a FROM Airport a WHERE a.airportName LIKE %?1% AND a.deletedAt IS NULL")
    List<Airport> findByAirportNameContaining(String airportName);
}
