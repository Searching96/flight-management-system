package com.flightmanagement.repository;

import com.flightmanagement.entity.Chatbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatboxRepository extends JpaRepository<Chatbox, Integer> {
    
    @Query("SELECT c FROM Chatbox c WHERE c.deletedAt IS NULL")
    List<Chatbox> findAllActive();
    
    @Query("SELECT c FROM Chatbox c WHERE c.customer.customerId = ?1 AND c.deletedAt IS NULL")
    List<Chatbox> findByCustomerId(Integer customerId);
    
    @Query("SELECT c FROM Chatbox c WHERE c.employee.employeeId = ?1 AND c.deletedAt IS NULL")
    List<Chatbox> findByEmployeeId(Integer employeeId);
    
    @Query("SELECT c FROM Chatbox c WHERE c.customer.customerId = ?1 AND c.employee.employeeId = ?2 AND c.deletedAt IS NULL")
    Optional<Chatbox> findByCustomerIdAndEmployeeId(Integer customerId, Integer employeeId);
}
