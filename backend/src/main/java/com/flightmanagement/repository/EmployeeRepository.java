package com.flightmanagement.repository;

import com.flightmanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    
    @Query("SELECT e FROM Employee e WHERE e.deletedAt IS NULL")
    List<Employee> findAllActive();
    
    @Query("SELECT e FROM Employee e WHERE e.employeeId = ?1 AND e.deletedAt IS NULL")
    Optional<Employee> findActiveById(Integer id);
    
    @Query("SELECT e FROM Employee e WHERE e.employeeType = ?1 AND e.deletedAt IS NULL")
    List<Employee> findByEmployeeType(Integer employeeType);
    
    @Query("SELECT e FROM Employee e JOIN e.account a WHERE a.email = ?1 AND e.deletedAt IS NULL")
    Optional<Employee> findByEmail(String email);
}
