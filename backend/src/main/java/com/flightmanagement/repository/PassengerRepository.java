package com.flightmanagement.repository;

import com.flightmanagement.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    
    @Query("SELECT p FROM Passenger p WHERE p.deletedAt IS NULL")
    List<Passenger> findAllActive();
    
    @Query("SELECT p FROM Passenger p WHERE p.passengerId = :id AND p.deletedAt IS NULL")
    Optional<Passenger> findActiveById(@Param("id") Integer id);
    
    @Query("SELECT p FROM Passenger p WHERE p.citizenId = :citizenId AND p.deletedAt IS NULL")
    Optional<Passenger> findByCitizenId(@Param("citizenId") String citizenId);
    
    @Query("SELECT p FROM Passenger p WHERE p.email = :email AND p.deletedAt IS NULL")
    List<Passenger> findByEmail(@Param("email") String email);
    
    @Query("SELECT p FROM Passenger p WHERE LOWER(p.passengerName) LIKE LOWER(CONCAT('%', :name, '%')) AND p.deletedAt IS NULL")
    List<Passenger> findByPassengerNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT p FROM Passenger p WHERE p.phoneNumber = :phoneNumber AND p.deletedAt IS NULL")
    List<Passenger> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
