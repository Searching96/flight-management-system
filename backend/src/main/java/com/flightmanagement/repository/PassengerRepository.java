package com.flightmanagement.repository;

import com.flightmanagement.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    
    @Query("SELECT p FROM Passenger p WHERE p.deletedAt IS NULL")
    List<Passenger> findAllActive();
    
    @Query("SELECT p FROM Passenger p WHERE p.passengerId = ?1 AND p.deletedAt IS NULL")
    Optional<Passenger> findActiveById(Integer id);
    
    @Query("SELECT p FROM Passenger p WHERE p.citizenId = ?1 AND p.deletedAt IS NULL")
    Optional<Passenger> findByCitizenId(String citizenId);
    
    @Query("SELECT p FROM Passenger p WHERE p.email = ?1 AND p.deletedAt IS NULL")
    List<Passenger> findByEmail(String email);
    
    @Query("SELECT p FROM Passenger p WHERE p.passengerName LIKE %?1% AND p.deletedAt IS NULL")
    List<Passenger> findByPassengerNameContaining(String passengerName);
}
