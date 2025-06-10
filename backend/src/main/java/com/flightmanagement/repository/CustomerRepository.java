package com.flightmanagement.repository;

import com.flightmanagement.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL")
    List<Customer> findAllActive();
    
    @Query("SELECT c FROM Customer c WHERE c.customerId = ?1 AND c.deletedAt IS NULL")
    Optional<Customer> findActiveById(Integer id);
    
    @Query("SELECT c FROM Customer c WHERE c.account.email = ?1 AND c.deletedAt IS NULL")
    Optional<Customer> findByEmail(String email);

    // Get customer score by id
    @Query("SELECT c.score FROM Customer c WHERE c.customerId = ?1 AND c.deletedAt IS NULL")
    Optional<Integer> findScoreById(Integer id);
}
