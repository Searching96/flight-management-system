package com.flightmanagement.repository;

import com.flightmanagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query("SELECT e FROM Employee e WHERE e.deletedAt IS NULL")
    List<Employee> findAllActive();

    Page<Employee> findByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.employeeId = :id AND e.deletedAt IS NULL")
    Optional<Employee> findActiveById(@Param("id") Integer id);

    @Query("SELECT e FROM Employee e WHERE e.employeeType = :type AND e.deletedAt IS NULL")
    List<Employee> findByEmployeeType(@Param("type") Integer type);

    @Query("SELECT e FROM Employee e JOIN e.account a WHERE a.email = :email AND e.deletedAt IS NULL")
    Optional<Employee> findByEmail(@Param("email") String email);

    @Query("SELECT e FROM Employee e WHERE e.account.accountId = :accountId AND e.deletedAt IS NULL")
    Optional<Employee> findByAccountId(@Param("accountId") Integer accountId);

    @Query("""
        SELECT e FROM Employee e JOIN e.account a 
        WHERE e.deletedAt IS NULL 
        AND (LOWER(a.accountName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) 
             OR LOWER(a.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(a.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(a.citizenId) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        """)
    List<Employee> searchEmployees(@Param("searchTerm") String searchTerm);
}